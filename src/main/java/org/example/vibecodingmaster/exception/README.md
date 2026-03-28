# Exception 处理层

本目录包含全局异常处理逻辑和自定义响应格式。

## 主要类说明

- **[GlobalExceptionHandler](file:///C:/Users/fuzeofficial/VibeCodingNow/VibeCodingMasters/src/main/java/org/example/vibecodingmaster/exception/GlobalExceptionHandler.java)**: 使用 `@RestControllerAdvice` 拦截系统中所有的异常。
  - 将 `EntityNotFoundException` 映射为 404 状态。
  - 将 `IllegalArgumentException` 映射为 400 状态。
- **[ErrorResponse](file:///C:/Users/fuzeofficial/VibeCodingNow/VibeCodingMasters/src/main/java/org/example/vibecodingmaster/dto/ErrorResponse.java)**: 统一的错误返回 JSON 格式。
