# Результаты производительности для 10,000 записей

| Операция | Тип данных | Кол-во записей | Время (мс) | Алгоритм/Подход | Примечания |
|----------|------------|----------------|-------------|------------------|-------------|
| INSERT | All | 40000 | 7092 | `Batch Save` | Пакетная вставка всех сущностей |
| SEARCH (Users by username) | User | 1 | 3 | `FindByUsername` | Поиск по уникальному имени |
| SEARCH (Users by email) | User | 1 | 2 | `FindByEmail` | Поиск по email |
| SORT (Users by name) | User | 10000 | 59 | `DB Sort` | Сортировка по username |
| SORT (Users by email) | User | 10000 | 46 | `DB Sort` | Сортировка по email |
| SORT (Functions by name) | Function | 10000 | 157 | `DB Sort` | Сортировка по name |
| SORT (Functions by points) | Function | 10000 | 90 | `DB Sort` | Сортировка по points_count |
| SORT (Operations by type) | Operation | 5000 | 65 | `DB Sort` | Сортировка по operation_type |
| SORT (Cache by access) | ComputationCache | 5000 | 40 | `DB Sort` | Сортировка по access_count |
| DELETE | All | 40000 | 13355 | `Delete All` | Массовое удаление всех сущностей |
