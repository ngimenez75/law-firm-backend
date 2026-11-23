package com.lawfirm.api.entity;

public enum Role {
    ROLE_ADMIN,      // Socios (acceso total y métricas financieras/estadísticas)
    ROLE_LAWYER,     // Abogados del estudio (Dra. Gómez, Dr. Martínez)
    ROLE_PARALEGAL,  // Procuradores / Colaboradores (Mile, Ale)
    ROLE_CLIENT      // Futuro acceso restringido para clientes
}
