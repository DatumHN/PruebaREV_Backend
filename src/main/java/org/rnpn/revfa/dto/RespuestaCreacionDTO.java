package org.rnpn.revfa.dto;


public class RespuestaCreacionDTO {
  public String message;
  public Object data;

  public RespuestaCreacionDTO(String message, Object data) {
    this.message = message;
    this.data = data;
  }
}
