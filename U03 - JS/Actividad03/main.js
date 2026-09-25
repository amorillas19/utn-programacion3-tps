// Base de datos simulada (nuestros usuarios)
const users = [
  { id: 1, name: "Ana" },
  { id: 2, name: "Luis" },
  { id: 3, name: "María" }
];

// ====================================
// ACTIVIDAD 1: CALLBACK
// ====================================
function getUserById(id, callback) {
  setTimeout(() => {
    const user = users.find(u => u.id === id);
    
    if (user) {
      callback(null, user);
    } else {
      callback("Usuario no encontrado", null);
    }
  }, 1500);
}

// Prueba de Callback
console.log("--- ACTIVIDAD 1: CALLBACK ---");
getUserById(2, (error, user) => {
  if (error) {
    console.error("Error:", error);
  } else {
    console.log("Usuario encontrado:", user);
  }
});

getUserById(99, (error, user) => {
  if (error) {
    console.error("Error:", error);
  } else {
    console.log("Usuario encontrado:", user);
  }
});


// ====================================
// ACTIVIDAD 2: PROMESAS
// ====================================
function getUserByIdPromise(id) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      const user = users.find(u => u.id === id);
      
      if (user) {
        resolve(user);
      } else {
        reject("Usuario no encontrado");
      }
    }, 1500);
  });
}

// Prueba de Promesas
console.log("\n--- ACTIVIDAD 2: PROMESAS ---");
getUserByIdPromise(3)
  .then(user => console.log("Usuario encontrado:", user))
  .catch(error => console.error("Error:", error));

getUserByIdPromise(5)
  .then(user => console.log("Usuario encontrado:", user))
  .catch(error => console.error("Error:", error));


// ====================================
// ACTIVIDAD 3: ASYNC/AWAIT
// ====================================
async function fetchUser(id) {
  try {
    const user = await getUserByIdPromise(id);
    console.log("Usuario obtenido con async/await:", user);
  } catch (error) {
    console.error("Error en async/await:", error);
  }
}

// Prueba de Async/Await
console.log("\n--- ACTIVIDAD 3: ASYNC/AWAIT ---");
fetchUser(1);
fetchUser(2);
fetchUser(99);
