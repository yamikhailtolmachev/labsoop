# API Performance Test Results

**Date:** 2025-12-20 22:48:24
**Iterations per endpoint:** 10
**Authentication:** Basic Auth
**User:** testuser_api

| Endpoint | Avg Time (ms) | Min (ms) | Max (ms) | Success Rate (%) |
|----------|--------------|----------|----------|-----------------|
| GET /api/cache | 121,40 | 74 | 186 | 100,0% |
| GET /api/functions | 926,60 | 843 | 1054 | 100,0% |
| GET /api/functions/search | 969,40 | 773 | 1161 | 100,0% |
| GET /api/operations | 218,70 | 160 | 315 | 100,0% |
| GET /api/users | 126,40 | 75 | 305 | 100,0% |

## Conclusions
1. Basic Auth filter works correctly
2. All GET endpoints are available
3. API performance is stable

