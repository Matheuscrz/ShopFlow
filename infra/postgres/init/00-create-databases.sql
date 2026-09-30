SELECT format('CREATE DATABASE %I', database_name)
FROM (
  VALUES
    ('identity_db'),
    ('catalog_db'),
    ('order_db'),
    ('notification_db')
) AS databases(database_name)
WHERE NOT EXISTS (
  SELECT 1
  FROM pg_database
  WHERE datname = database_name
)\gexec

SELECT format('ALTER DATABASE %I OWNER TO shopflow_admin', database_name)
FROM (
  VALUES
    ('identity_db'),
    ('catalog_db'),
    ('order_db'),
    ('notification_db')
) AS databases(database_name)\gexec