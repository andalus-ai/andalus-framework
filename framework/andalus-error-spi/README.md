# Andalus Error SPI

The `andalus-error-spi` module standardizes error reporting at integration
boundaries. It defines error codes, structured error responses, Andalus
exceptions, and exception mappers for converting failures into API responses.

Register or compose `AndalusExceptionMapper` implementations in the host
runtime. Use stable `ErrorCode` values for client-visible failures and reserve
the unexpected-exception mapper for errors without a known classification.
