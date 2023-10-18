package com.wisol.wisolapp

data class ClientesProductosModel(val id_cliente:String,
                                  val desc_cliente:String,
                                  val vendedor:String,
                                  val id_producto:String,
                                  var isSelected: Boolean = false)
