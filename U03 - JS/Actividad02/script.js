// PUNTO 1: FUNCIONES DECLARATIVAS VS EXPRESADAS

// FUNCIÓN DECLARATIVA
function cuadrado(numero) {
  return numero * numero;
}

// FUNCIÓN EXPRESADA
const cubo = function(numero) {
  return numero * numero * numero;
};

// Pruebas
console.log("Cuadrado de 5:", cuadrado(5));
console.log("Cubo de 5:", cubo(5));

console.log("\n=== DIFERENCIA ===");
console.log("Declarativa: function nombre() { }");
console.log("Expresada: const nombre = function() { }");
console.log("\nLa declarativa se puede usar antes de definirla.");
console.log("La expresada solo después de definirla.");

// PUNTO 2: ARROW FUNCTIONS Y PARÁMETROS POR DEFECTO

console.log("\n=== ARROW FUNCTIONS ===\n");

const saludar = (nombre, edad = 18) => {
  return "Hola " + nombre + ", tienes " + edad + " años";
};

// Pruebas
console.log(saludar("Ana"));           // Usa edad por defecto (18)
console.log(saludar("Carlos", 25));    // Especifica la edad
console.log(saludar("María", 30));

console.log("\n=== QUÉ ES UNA ARROW FUNCTION ===");
console.log("Sintaxis: const nombre = (param1, param2 = default) => { };");
console.log("Es una forma más compacta que function() { }");
console.log("Los parámetros pueden tener valores por defecto.");

// PUNTO 3: OBJETO CON PROPIEDADES Y MÉTODOS
const persona = {
  nombre: "Ana",
  edad: 25,
  presentarse: function() {
    return "Hola, soy " + this.nombre + " y tengo " + this.edad + " años";
  }
};

console.log("=== PUNTO 3: OBJETOS ===\n");
console.log(persona.presentarse());

console.log("\n=== DIFERENCIA ===");
console.log("Propiedades: datos del objeto (nombre, edad)");
console.log("Métodos: funciones dentro del objeto que actúan sobre esos datos");

// PUNTO 4: OBJETO PARA DESESTRUCTURAR
const personaDesestructurada = {
  nombre: "Ana",
  edad: 25,
  ciudad: "Mendoza"
};

console.log("=== PUNTO 4: DESESTRUCTURACIÓN ===\n");

const { nombre, edad, ciudad } = personaDesestructurada;

console.log("Nombre:", nombre);
console.log("Edad:", edad);
console.log("Ciudad:", ciudad);

console.log("\n=== SIN DESESTRUCTURACIÓN (forma vieja) ===");
console.log("Nombre:", personaDesestructurada.nombre);
console.log("Edad:", personaDesestructurada.edad);
console.log("Ciudad:", personaDesestructurada.ciudad);

console.log("\n=== VENTAJAS DE DESESTRUCTURACIÓN ===");
console.log("1. Código más limpio y legible");
console.log("2. Menos repetición (no repites 'personaDesestructurada.' cada vez)");
console.log("3. Más fácil de mantener");
console.log("4. Las variables son locales y directas");

// PUNTO 5: SPREAD: expande un array
const numeros = [1, 2, 3];
const numerosExtendidos = [...numeros, 4, 5, 6];

console.log("=== PUNTO 5: SPREAD/REST ===\n");
console.log("Array original:", numeros);
console.log("Array con spread:", numerosExtendidos);

// REST: recibe múltiples argumentos en una función
function suma(...numeros) {
  let total = 0;
  for (let num of numeros) {
    total += num;
  }
  return total;
}

console.log("\nSuma de 2, 3, 5:", suma(2, 3, 5));
console.log("Suma de 10, 20, 30, 40:", suma(10, 20, 30, 40));

console.log("\n=== DIFERENCIA ===");
console.log("Spread (...): expande un array/objeto en elementos individuales");
console.log("Rest (...): recoge múltiples argumentos en un array dentro de una función");
console.log("- Spread se usa al PASAR datos");
console.log("- Rest se usa al RECIBIR datos");


// ============ DECLARAR TODAS LAS VARIABLES AL INICIO ============
const titulo = document.getElementById("titulo");
const lista = document.getElementById("lista");
const inputTexto = document.getElementById("inputTexto");
const botonAgregar = document.getElementById("botonAgregar");
const formulario = document.getElementById("formulario");
const inputFormulario = document.getElementById("inputFormulario");
const mensajeDiv = document.getElementById("mensaje");
const inputKeydown = document.getElementById("inputKeydown");
const selectOpciones = document.getElementById("selectOpciones");
const parrafoSeleccion = document.getElementById("parrafoSeleccion");
const listaKeydown = document.getElementById("listaKeydown");

// PUNTO 6: MANIPULACIÓN DEL DOM
console.log("=== PUNTO 6: MANIPULACIÓN DEL DOM ===\n");

console.log("Título original:", titulo.textContent);
titulo.textContent = "Mi Lista Interactiva";
console.log("Título nuevo:", titulo.textContent);

const item1 = document.createElement("li");
item1.textContent = "Elemento agregado con JS";
lista.appendChild(item1);

const item2 = document.createElement("li");
item2.textContent = "Otro elemento más";
lista.appendChild(item2);

console.log("\nClases del título:", titulo.className);
titulo.classList.add("destacado");
console.log("Después de add:", titulo.className);

titulo.classList.remove("destacado");
console.log("Después de remove:", titulo.className);

titulo.classList.toggle("destacado");
console.log("Después de toggle:", titulo.className);

console.log("\n=== MÉTODOS DEL DOM ===");
console.log("getElementById(): selecciona por id");
console.log("textContent: cambia el texto del elemento");
console.log("createElement(): crea un nuevo elemento");
console.log("appendChild(): agrega un elemento como hijo");
console.log("classList.add/remove/toggle(): maneja clases CSS");

// PUNTO 7: EVENTOS CLICK E INPUT
console.log("\n=== PUNTO 7: EVENTOS CLICK E INPUT ===\n");

botonAgregar.addEventListener("click", function() {
  const texto = inputTexto.value;
  
  if (texto.trim() === "") {
    alert("Por favor ingresa algo");
    return;
  }
  
  const nuevoItem = document.createElement("li");
  nuevoItem.textContent = texto;
  lista.appendChild(nuevoItem);
  
  inputTexto.value = "";
  inputTexto.focus();
  
  console.log("Se agregó:", texto);
});

console.log("=== EVENTO CLICK ===");
console.log("- Se dispara cuando el usuario hace clic en un elemento");
console.log("- Usamos addEventListener('click', function)");
console.log("- Sintaxis: elemento.addEventListener('click', callback)");
console.log("- Se ejecuta una sola vez por cada clic");

// PUNTO 8: EVENTO SUBMIT Y PREVENTDEFAULT
console.log("\n=== PUNTO 8: EVENTO SUBMIT Y PREVENTDEFAULT ===\n");

formulario.addEventListener("submit", function(event) {
  event.preventDefault();
  
  const valor = inputFormulario.value;
  
  if (valor.trim() === "") {
    alert("Por favor ingresa algo");
    return;
  }
  
  mensajeDiv.textContent = "Ingresaste: " + valor;
  mensajeDiv.style.color = "green";
  
  console.log("Formulario enviado con:", valor);
  
  inputFormulario.value = "";
  inputFormulario.focus();
});

console.log("=== DIFERENCIA ENTRE EVENTOS ===");
console.log("click: se dispara cuando haces clic en un botón");
console.log("submit: se dispara cuando envías un formulario");
console.log("preventDefault(): detiene el comportamiento por defecto (recarga)");
console.log("\nSin preventDefault, la página se recarga y pierdes los datos.");
console.log("Con preventDefault, puedes manejar el envío con JavaScript.");

// PUNTO 9: EVENTOS KEYDOWN Y CHANGE
console.log("\n=== PUNTO 9: EVENTOS KEYDOWN Y CHANGE ===\n");

inputKeydown.addEventListener("keydown", function(event) {
  if (event.key === "Enter") {
    const texto = inputKeydown.value;
    
    if (texto.trim() === "") {
      alert("Por favor ingresa algo");
      return;
    }
    
    const nuevoItem = document.createElement("li");
    nuevoItem.textContent = texto;
    listaKeydown.appendChild(nuevoItem);
    
    console.log("Se agregó con Enter:", texto);
    inputKeydown.value = "";
  }
});

selectOpciones.addEventListener("change", function(event) {
  const opcionSeleccionada = event.target.value;
  parrafoSeleccion.textContent = "Seleccionaste: " + opcionSeleccionada;
  console.log("Opción seleccionada:", opcionSeleccionada);
});

console.log("=== DIFERENCIA ENTRE EVENTOS ===");
console.log("input: se dispara mientras escribes (en cada carácter)");
console.log("keydown: se dispara cuando presionas una tecla");
console.log("change: se dispara cuando terminas de cambiar (select, radio, checkbox)");
console.log("\nUsos:");
console.log("- input: validar en tiempo real mientras escribes");
console.log("- keydown: detectar teclas específicas (Enter, Escape, etc)");
console.log("- change: cuando el usuario termina de cambiar un select/radio");