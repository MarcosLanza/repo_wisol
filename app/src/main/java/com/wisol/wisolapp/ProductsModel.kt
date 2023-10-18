package com.wisol.wisolapp

data class ProductsModel(
    val vendedor: String,
    val id_cliente: String,
    val desc_cliente: String,
    val id_producto: String,
    val desc_producto: String,
    val marca: String,
    val tipo: String,
    val precio: String,
    val descuento: String,
    val tipo_impuesto: String,
    val tipo_tarifa: String,
    val impuesto: String,
    val venta_trim: String,
    val venta_ant: String,
    val venta_act: String,
    val bono: String,
    val minimo: String
)

