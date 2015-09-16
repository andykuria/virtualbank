Window application
Written in Java
Support Iso8583 message with many kind of format (contain different type of header; contain different kind of field - hex/bytes/ascii...)
Allow 3 main services
- ACQ services: compose message in xml and generate message as bank format
- Switching service: can switching message from on Port to another port (pairing mode) and message routing by PAN
- Iss service: can allow response message from ACQ in many cases; auto response, delay response, can manage account balance
Also support MAC, PIN, connectivity to HSM, network message...