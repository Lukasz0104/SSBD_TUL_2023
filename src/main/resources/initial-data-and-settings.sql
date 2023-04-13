GRANT SELECT, INSERT, UPDATE ON TABLE account TO ssbd05mok;
GRANT SELECT, INSERT, DELETE ON TABLE access_level TO ssbd05mok;
GRANT SELECT, INSERT, UPDATE ON TABLE account_data TO ssbd05mok;
GRANT SELECT, INSERT, UPDATE ON TABLE admin_data TO ssbd05mok;
GRANT SELECT, INSERT, UPDATE ON TABLE manager_data TO ssbd05mok;
GRANT SELECT, INSERT, UPDATE ON TABLE owner_data TO ssbd05mok;

CREATE VIEW auth_view AS SELECT login, password FROM account a WHERE a.active = TRUE AND a.verified = TRUE;

GRANT SELECT ON auth_view TO ssbd05auth;

GRANT SELECT, INSERT, UPDATE ON TABLE building TO ssbd05mow;
GRANT SELECT ON TABLE category TO ssbd05mow;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE cost TO ssbd05mow;
GRANT SELECT, INSERT, UPDATE ON TABLE forecast TO ssbd05mow;
GRANT SELECT ON TABLE meter TO ssbd05mow;
GRANT SELECT, INSERT, UPDATE ON TABLE place TO ssbd05mow;
GRANT SELECT, INSERT, DELETE ON TABLE place_owner TO ssbd05mow;
GRANT SELECT, INSERT, DELETE ON TABLE place_rate TO ssbd05mow;
GRANT SELECT, INSERT ON TABLE rate TO ssbd05mow;
GRANT SELECT, INSERT ON TABLE reading TO ssbd05mow;
GRANT SELECT, INSERT ON TABLE report TO ssbd05mow;

GRANT SELECT, UPDATE ON SEQUENCE access_level_id_seq TO ssbd05mok;
GRANT SELECT, UPDATE ON SEQUENCE account_id_seq TO ssbd05mok;

GRANT SELECT, UPDATE ON SEQUENCE building_id_seq TO ssbd05mow;
GRANT SELECT, UPDATE ON SEQUENCE category_id_seq TO ssbd05mow;
GRANT SELECT, UPDATE ON SEQUENCE cost_id_seq TO ssbd05mow;
GRANT SELECT, UPDATE ON SEQUENCE forecast_id_seq TO ssbd05mow;
GRANT SELECT, UPDATE ON SEQUENCE meter_id_seq TO ssbd05mow;
GRANT SELECT, UPDATE ON SEQUENCE place_id_seq TO ssbd05mow;
GRANT SELECT, UPDATE ON SEQUENCE rate_id_seq TO ssbd05mow;
GRANT SELECT, UPDATE ON SEQUENCE reading_id_seq TO ssbd05mow;
GRANT SELECT, UPDATE ON SEQUENCE report_id_seq TO ssbd05mow;

CREATE INDEX access_level_account_id ON access_level USING btree (account);
CREATE INDEX cost_category_id ON cost USING btree (category_id);
CREATE INDEX forecast_place_id ON forecast USING btree (place_id);
CREATE INDEX forecast_rate_id ON forecast USING btree (rate_id);
CREATE INDEX meter_category_id ON meter USING btree (category_id);
CREATE INDEX meter_place_id ON meter USING btree (place_id);
CREATE INDEX place_building_id ON place USING btree (building_id);
CREATE INDEX place_owner_owner_id ON place_owner USING btree (owner_id);
CREATE INDEX place_owner_place_id ON place_owner USING btree (place_id);
CREATE INDEX place_rate_place_id ON place_rate USING btree (place_id);
CREATE INDEX place_rate_rate_id ON place_rate USING btree (rate_id);
CREATE INDEX rate_category_id ON rate USING btree (category_id);
CREATE INDEX reading_meter_id ON reading USING btree (meter_id);
CREATE INDEX report_category_id ON report USING btree (category_id);
CREATE INDEX report_place_id ON report USING btree (place_id);
