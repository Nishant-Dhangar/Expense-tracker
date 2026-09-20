// ==============================
// LOGIN
// ==============================

document
    .getElementById("loginForm")
    .addEventListener("submit", async function(event) {

        event.preventDefault();


        const email =
            document.getElementById("loginEmail").value;

        const password =
            document.getElementById("loginPassword").value;

        const message =
            document.getElementById("loginMessage");


        try {

            const response = await fetch(
                "/api/auth/login",
                {
                    method: "POST",

                    headers: {
                        "Content-Type": "application/json"
                    },

                    body: JSON.stringify({
                        email: email,
                        password: password
                    })
                }
            );


            if (!response.ok) {

                const error =
                    await response.text();

                message.textContent = error;

                return;
            }


            const user =
                await response.json();


            // Save logged-in user
            window.location.href = "index.html";


        } catch (error) {

            console.error(error);

            message.textContent =
                "Unable to connect to server.";

        }

    });