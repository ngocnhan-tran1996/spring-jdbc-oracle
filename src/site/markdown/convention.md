# Convention

<!-- TOC -->
* [Convention](#convention)
  * [Data Convention](#data-convention)
  * [Naming Convention](#naming-convention)
  * [Annotation OracleParameter](#annotation-oracleparameter)
<!-- TOC -->

## Data Convention

| **Type Data** | **Java Data** |
|---------------|---------------|
| NUMBER        | BigDecimal    |
| VARCHAR       | String        |
| DATE          | Timestamp     |


## Naming Convention

I have a small comparison.

| **Type Name** | **Field Name** |
|---------------|----------------|
| last_name     | last_name      |
| last_name     | lastName       |
| LAST_NAME     | lastName       |
| LASTNAME      | lastName       |


## Annotation OracleParameter

Use annotation `OracleParameter` in case both field name are not match.