# Wayang Runtime SPI

The `wayang-runtime-spi` module defines runtime extension points for execution
and tool integration. It allows a runtime to provide execution services and
tool services without coupling framework contracts to one implementation.

Implement the SPI in the runtime and register it through the standard service
provider mechanism where applicable.
