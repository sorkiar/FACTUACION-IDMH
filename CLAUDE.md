# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
mvn clean package -DskipTests

# Run (production profile)
mvn spring-boot:run

# Run (dev profile — uses idmhperu_test DB, show-sql=true, recaptcha bypassed)
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Run tests
mvn test

# Run single test class
mvn test -Dtest=SaleServiceImplTest

# Compile only (fast check)
mvn compile
```

Server runs on port **8090**. Swagger UI: `http://localhost:8090/swagger-ui/index.html`.

## Architecture

Spring Boot 4.x + Java 17 REST API for Peruvian electronic invoicing (SUNAT).

### Package layout

```
com.service.api.idmhperu
├── configuration/      # SecurityConfig, AppConfig, OpenApiConfig
├── controller/         # REST controllers
├── dto/
│   ├── entity/         # JPA entities (placed under dto/ by convention)
│   ├── external/       # DTOs sent to external facturador SUNAT service
│   ├── filter/         # Filter POJOs used by Specifications
│   ├── mapper/         # MapStruct mappers
│   ├── request/        # Inbound request DTOs
│   └── response/       # Outbound response DTOs
├── exception/          # GlobalExceptionHandler, BusinessValidationException, ResourceNotFoundException
├── job/                # Scheduled jobs (SunatDocumentJobService, ExchangeRateJobService)
├── repository/
│   └── spec/           # JPA Specifications for filtered queries
├── security/           # JWT filter, AuthService, UserDetailsService
├── service/            # Service interfaces
│   └── impl/           # Service implementations
└── util/               # JwtUtils, InvoicingUtils, GoogleDriveOAuthUtils, ClientValidator
```

### Key patterns

- All responses wrapped in `ApiResponse<T>`
- Filters: pass `XFilter` POJO → `XSpecification` (JPA Specification) → repository
- Soft delete: entities have `deletedAt`; queries filter `deletedAt IS NULL`
- Audit fields: `createdAt/createdBy/updatedAt/updatedBy/deletedAt/deletedBy`; `createdBy` set via `JwtUtils.extractUsernameFromContext()`
- Entities use Lombok `@Getter/@Setter/@NoArgsConstructor/@AllArgsConstructor`
- MapStruct mappers: `@Mapper(componentModel = "spring")`
- Status toggling via `PATCH` endpoints with `XStatusRequest` body

### SUNAT invoicing flow

1. Sale/Note/Guide is created with `status = PENDIENTE`
2. `SunatDocumentJobService` runs every 30 min (`@Scheduled`) and picks up `PENDIENTE` documents
3. Job builds `SunatSendRequest` → posts to external facturador at `${sunat.url}` (or `${sunat.url-guia}` for guides)
4. On success: status → `ACEPTADO`, PDF generated via JasperReports, uploaded to Google Drive, `pdfUrl` stored
5. Logs each attempt in `sunat_request_log` table

### PDF generation

JasperReports templates in `src/main/resources/jasper/`:
- `FacturaA4.jrxml`, `BoletaA4.jrxml` — invoices
- `NotaCreditoDebitoA4.jrxml` — credit/debit notes
- `GuiaRemisionElectronicaA4.jrxml` — remission guides
- `ReporteVentasA4.jrxml`, `CotizacionA4.jrxml`, `ServicioFichaA4.jrxml`

PDFs use `JRBeanCollectionDataSource` (not SQL). Compiled at runtime via `JasperCompileManager`.

### Database migrations

Flyway. Files in `src/main/resources/db/migration/` named `V###__description.sql`. Currently at V041. Always add a new `V(N+1)__description.sql` — never edit existing migrations.

### External integrations

- **SUNAT facturador**: `https://e-fact.facturacion-idmhperu.website/facturador/...` — separate service that handles SUNAT communication
- **apiperu.dev**: DNI/RUC lookup (responses cached in `dni_record`/`ruc_record` tables)
- **Google Drive**: PDFs and payment proofs uploaded via OAuth2 service account (`client_secret.json`). Folder IDs per document type in `application.yml` under `drive.folder-id`
- **Exchange rate**: BCB API, fetched and stored by `ExchangeRateJobService`

### Business rules (critical)

- **Language**: requirements discussed in Spanish; ALL code (fields, columns, endpoints, variables) in English
- **Document types**: `"01"` FACTURA, `"03"` BOLETA, `"07"` NOTA_CREDITO, `"08"` NOTA_DEBITO, `"09"` GUIA_REMISION_REMITENTE
- **Bimoneda**: PEN and USD supported. `detractionAmount` and `retentionAmount` always stored in PEN. SUNAT `itcoRetencion` must be in invoice currency (not converted)
- **Detraction (SPOT)**: only for FACTURA/BOLETA; total in PEN must exceed `min_detraccion_amount` config. USD invoices: convert to PEN for amount calculation; `lsDetraccion` block omitted for USD in SUNAT request (error 3208)
- **Credit sales**: `CREDITO` payment type requires installments; sum of installments must equal `totalAmount - detractionAmountLocal`
- **Notes**: original Sale must be `ACEPTADO` to create a credit/debit note; note inherits `currencyCode` from Sale
- **Configuration**: runtime settings stored in `configuration` table, accessed via `ConfigurationService.getValue(key)`

### Security

Stateless JWT. Public endpoints: `/api/auth/**`, `/api/document-types/**`, Swagger. All others require `Authorization: Bearer <token>`.
