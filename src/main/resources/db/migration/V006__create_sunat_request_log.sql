CREATE TABLE sunat_request_log (
    id              BIGSERIAL PRIMARY KEY,
    document_type   VARCHAR(5)   NOT NULL,
    series          VARCHAR(10)  NOT NULL,
    sequence        VARCHAR(10)  NOT NULL,
    triggered_by    VARCHAR(50)  NOT NULL,
    sent_at         TIMESTAMP    NOT NULL,
    request_json    TEXT         NOT NULL,
    response_json   TEXT,
    http_status     INTEGER,
    success         BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_srl_document ON sunat_request_log (document_type, series, sequence);
CREATE INDEX idx_srl_sent_at  ON sunat_request_log (sent_at DESC);
