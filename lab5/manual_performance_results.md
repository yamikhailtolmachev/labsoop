# Результаты тестирования производительности поиска и сортировки

## Manual JDBC Поиск и Сортировка

| Операция | Обработано записей | Время (мс) | Записей/сек |
|----------|-------------------|------------|-------------|
| Single Search (by user) | 2000 | 88 | 22727,27 |
| Multiple Criteria Search | 200 | 186 | 1075,27 |
| Depth-First Search | 511 | 232 | 2202,59 |
| Breadth-First Search | 0 | 57 | 0,00 |
| Hierarchy Search | 1000 | 294 | 3401,36 |
| SORT Functions by name | 2000 | 181 | 11049,72 |
| SORT Functions by type | 2000 | 63 | 31746,03 |
| SORT Functions by points | 2000 | 60 | 33333,33 |
| SORT Cache by access | 1000 | 60 | 16666,67 |
| SORT Operations by date | 1000 | 63 | 15873,02 |
