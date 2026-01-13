# Отчет о производительности API (Newman)

| Название запроса | Время выполнения (мс) |
|------------------|------------------------|
| Setup - Generate Timestamp | 146 |
| 1.1 Users - POST (Create First User) | 208 |
| 1.2 Users - GET by ID | 37 |
| 1.3 Users - GET all with Sort | 37 |
| 1.4 Users - PUT (Update) | 29 |
| 1.5 Users - GET by Username (Search) | 14 |
| 2.1 Functions - POST (Create) | 36 |
| 2.2 Functions - GET by ID | 18 |
| 2.3 Functions - GET by User ID with Sort | 21 |
| 2.4 Functions - PUT (Update) | 18 |
| 3.1 Operations - POST (Create) | 34 |
| 3.2 Operations - GET by ID | 14 |
| 3.3 Operations - GET by User ID | 16 |
| 4.1 Cache - POST (Create) | 36 |
| 4.2 Cache - GET by ID | 12 |
| 4.3 Cache - GET by Cache Key | 15 |
| 5.1 Cache - DELETE | 200 |
| 5.2 Operations - DELETE | 22 |
| 5.3 Functions - DELETE | 21 |
| 5.4 Users - DELETE | 18 |
| Generate Performance Report | 8 |

**Общее время выполнения всех запросов:** 960 мс
**Количество запросов:** 21
