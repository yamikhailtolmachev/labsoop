# Результаты производительности для 10,000 записей

| Операция | Тип данных | Кол-во записей | Время (мс) | Алгоритм/Подход | Примечания |
|----------|------------|----------------|-------------|------------------|-------------|
| INSERT | All | 40000 | 6579 | `Batch Save` | Пакетная вставка всех сущностей |
| SEARCH (Users by username) | User | 1 | 88 | `FindByUsername` | Поиск по уникальному имени |
| SEARCH (Users by email) | User | 1 | 15 | `FindByEmail` | Поиск по email |
| SORT (Users by name) | User | 10000 | 74 | `DB Sort` | Сортировка по username |
| SORT (Users by email) | User | 10000 | 43 | `DB Sort` | Сортировка по email |
| SORT (Functions by name) | Function | 10000 | 47 | `DB Sort` | Сортировка по name |
| SORT (Functions by points) | Function | 10000 | 27 | `DB Sort` | Сортировка по points_count |
| SORT (Operations by type) | Operation | 5000 | 15 | `DB Sort` | Сортировка по operation_type |
| SORT (Cache by access) | ComputationCache | 5000 | 21 | `DB Sort` | Сортировка по access_count |
| DELETE | All | 40000 | 71 | `Delete All` | Массовое удаление всех сущностей |
