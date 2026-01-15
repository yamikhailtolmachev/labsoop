# Результаты производительности для 10,000 записей

| Операция | Тип данных | Кол-во записей | Время (мс) | Алгоритм/Подход | Примечания |
|----------|------------|----------------|-------------|------------------|-------------|
| INSERT | All | 40000 | 11425 | `Batch Save` | Пакетная вставка всех сущностей |
| SEARCH (Users by username) | User | 1 | 106 | `FindByUsername` | Поиск по уникальному имени |
| SEARCH (Users by email) | User | 1 | 31 | `FindByEmail` | Поиск по email |
| SORT (Users by name) | User | 10000 | 95 | `DB Sort` | Сортировка по username |
| SORT (Users by email) | User | 10000 | 68 | `DB Sort` | Сортировка по email |
| SORT (Functions by name) | Function | 10000 | 115 | `DB Sort` | Сортировка по name |
| SORT (Functions by points) | Function | 10000 | 61 | `DB Sort` | Сортировка по points_count |
| SORT (Operations by type) | Operation | 5000 | 28 | `DB Sort` | Сортировка по operation_type |
| SORT (Cache by access) | ComputationCache | 5000 | 30 | `DB Sort` | Сортировка по access_count |
| DELETE | All | 40000 | 135 | `Delete All` | Массовое удаление всех сущностей |
