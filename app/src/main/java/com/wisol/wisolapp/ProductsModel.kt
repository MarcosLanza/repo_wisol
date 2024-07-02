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
    val minimo: String,
    val uc: String,
    val ordernar:String
){
    override fun toString(): String {
        return "ProductsModel(vendedor='$vendedor', idCliente='$id_cliente', descCliente='$desc_cliente', " +
                "idProducto='$id_producto', descProducto='$desc_producto', marca='$marca', tipo='$tipo', " +
                "precio='$precio', descuento='$descuento', tipoImpuesto='$tipo_impuesto', tipoTarifa='$tipo_tarifa', " +
                "impuesto='$impuesto', ventaTrim='$venta_trim', ventaAnt='$venta_ant', ventaAct='$venta_act', " +
                "bono='$bono', minimo='$minimo', uc='$uc')"
    }
}

