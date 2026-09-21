# Andalus Database

The `andalus-database` module provides a small persistence abstraction for
Andalus services. It models entities, columns, identifiers, relationships,
repositories, transactions, migrations, row mapping, and pagination.

Implement `DatabaseService` and `EntityManager` for the database technology
used by the host application. Keep domain repositories dependent on the
interfaces in this module so persistence remains replaceable.
