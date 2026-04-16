ALTER TABLE `user`
    ADD COLUMN plain_password VARCHAR(50) NULL AFTER password;
