package org.rnpn.revfa.service;

import java.util.List;

import org.rnpn.revfa.dto.CampoDTO;
import org.rnpn.revfa.entity.Campo;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CampoService {
  /*
   * @WithTransaction public Uni<List<CampoDTO>> getCamposBySeccion(String id) { return
   * Campo.findByIdSeccion(id); }
   */

  @WithTransaction
  public Uni<List<CampoDTO>> getCamposBySeccion2(String id) {
    return Campo.seleccionarTodo(id);
  }



}
