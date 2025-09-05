package org.rnpn.revfa.service;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.rnpn.revfa.entity.Catalogos;

@ApplicationScoped
public class CatalogoService {


  @WithTransaction
  public Uni<Catalogos> getCatalogoHijo(Long id) {
    return Catalogos.findCatalogoHijo(id);
  }



}
