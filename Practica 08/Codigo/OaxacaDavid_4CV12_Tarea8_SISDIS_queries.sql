drop database ecommerce;
create database ecommerce;
use ecommerce;

CREATE TABLE articulos
(
	id_articulo integer auto_increment primary key,
	nombre varchar(500) not null,
	descripcion varchar(500) not null,
	precio double not null,
	cantidad_almacen integer not null
);

CREATE TABLE foto_articulos
(
	id_foto integer auto_increment primary key,
	foto longblob,
	id_articulo integer not null
);

CREATE TABLE carrito_compra(
	id_carrito_articulo integer auto_increment primary key,
	id_articulo integer not null,
	cantidad integer not null	
);

alter table foto_articulos add foreign key (id_articulo) references articulos(id_articulo);
alter table carrito_compra add foreign key (id_articulo) references articulos(id_articulo);

INSERT INTO articulos(id_articulo, nombre, descripcion, precio, cantidad_almacen) VALUES (0,"Telefono","Telefono Smartphone ",800.00,30);
INSERT INTO articulos(id_articulo, nombre, descripcion, precio, cantidad_almacen) VALUES (0,"Tableta","Tableta Smartphone Samsung",10000.00,15);
INSERT INTO articulos(id_articulo, nombre, descripcion, precio, cantidad_almacen) VALUES (0,"Refrigerados","Refrigerador Samsung",1800.00,5);

INSERT INTO foto_articulos VALUES (0,01010101010,1);
INSERT INTO foto_articulos VALUES (0,11010101010,2);
INSERT INTO foto_articulos VALUES (0,01111111110,3);

INSERT INTO carrito_compra(id_carrito_articulo, id_articulo, cantidad) VALUES (0,1,7);
INSERT INTO carrito_compra(id_carrito_articulo, id_articulo, cantidad) VALUES (0,2,5);

-- SELECT * FROM carrito_compra;
-- SELECT * FROM foto_articulos;
-- SELECT a.id_articulo, a.nombre, a.descripcion, a.precio, a.cantidad_almacen, b.foto FROM articulos a LEFT OUTER JOIN foto_articulos b ON a.id_articulo = b.id_articulo WHERE a.descripcion LIKE '%Samsung%';

-- SELECT a.id_articulo, a.nombre, a.descripcion, a.precio, b.cantidad, c.foto FROM carrito_compra b LEFT OUTER JOIN articulos a ON a.id_articulo = b.id_articulo LEFT OUTER JOIN foto_articulos c ON b.id_articulo = c.id_articulo;
