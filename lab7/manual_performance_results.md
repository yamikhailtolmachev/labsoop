# Результаты тестирования производительности поиска и сортировки

## Manual JDBC Поиск и Сортировка

| Операция | Обработано записей | Время (мс) | Записей/сек |
|----------|-------------------|------------|-------------|
| Single Search (by user) | 2000 | 74 | 27027,03 |
| Multiple Criteria Search | 200 | 84 | 2380,95 |
| Depth-First Search | 511 | 110 | 4645,45 |
| Breadth-First Search | 0 | 79 | 0,00 |
| Hierarchy Search | 1000 | 284 | 3521,13 |
| SORT Functions by name | 2000 | 52 | 38461,54 |
| SORT Functions by type | 2000 | 47 | 42553,19 |
| SORT Functions by points | 2000 | 50 | 40000,00 |
| SORT Cache by access | 1000 | 45 | 22222,22 |
| SORT Operations by date | 1000 | 43 | 23255,81 |
