<!DOCTYPE html>
<html>
    <meta charset="UTF-8">
    <head>
        <title>${titulo}</title>
        <style>
            /*Títulos de la página de login*/
            .titulo{
                background-color: lightgray;
                text-align: center;
                padding: 20px;
                height: 50%;
                width: 50%;
                margin: auto;
                color:white;
                border-radius: 50%;
            }
            /*Contenido del formulario de login*/
            .loginContainer {
                padding: 50px;
            }
            /*Campos del formulario*/
            input[type=text], input[type=password] {
                width: 100%;
                padding: 12px 20px;
                margin: 10px 0;
                display: inline-block;
                border: 1px solid #ccc;
                box-sizing: border-box;
            }
            /*Botón de registrarse*/
            button {
                background-color: lightgreen;
                color: black;
                padding: 12px 20px;
                margin: 10px 0 ;
                border: none;
                cursor: pointer;
                width: 100%;
            }
            button:hover {
                opacity: 0.8;
                color: whitesmoke;
            }
        </style>
    </head>
    <body>
    <div class="titulo">
        <h1> A&E Blog de Artículos</h1>
        <h2> Iniciar Sesión</h2>
    </div>
    <form action="/procesarUsuario" method="post">
        <div class="loginContainer">
            <label><b>Nombre de usuario</b></label>
            <input type="text" name="username" placeholder="Introduzca su nombre de usuario" required>

            <label><b>Contraseña</b></label>
            <input type="password" name="password" placeholder="Introduzca su contraseña"  required>

            <button type="submit">Registrarse</button>
            <label>
                <input type="checkbox" name="recordar"> Recordarme
            </label>
        </div>
    </form>
    </body>
</html>