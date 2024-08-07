package com.wisol.wisolapp

data class ProductosModel(val vendedor: String,
                          val id_cliente: String,
                          val desc_cliente: String,
                          val id_producto: String,
                          val desc_producto: String,
                          val marca: String,
                          val tipo: String,
                          var precio: String,
                          val descuento: String,
                          val tipo_impuesto: String,
                          val tipo_tarifa: String,
                          val impuesto: String,
                          val venta_trim: String,
                          val venta_ant: String,
                          val venta_act: String,
                          val bono: String,
                          val minimo: String,
                          var cnt: String,
                          var estado: String,
                          var idPedido: String,
                          var fecha:String,
                          val codigo_producto:String,
                          var comentario:String,
                          val numPedido:String,
                          var precioTotal: String,
                          var bonoT:String,
                          var uc:String,
                          val ordernar:String
)


