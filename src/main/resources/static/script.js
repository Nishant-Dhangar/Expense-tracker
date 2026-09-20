
// ==============================
// CURRENT USER
// ==============================

let USER_ID = null;
let loggedInUser = null;


// ==============================
// INITIALIZE DASHBOARD
// ==============================

document.addEventListener("DOMContentLoaded", async () => {

    try {

        // Get the currently authenticated user
        const response =
            await fetch("/api/auth/me");

        // User is not logged in
        if (!response.ok) {

            window.location.replace("login.html");
            return;
        }


        // Get user information
        loggedInUser =
            await response.json();

        USER_ID =
            loggedInUser.id;


        // Display username
        const userName =
            document.getElementById("userName");

        if (userName) {

            userName.textContent =
                loggedInUser.name;
        }


        // Load dashboard data
        await loadTransactions();
        await loadCategories();


        // Set today's date
        const dateInput =
            document.getElementById("date");

        if (dateInput) {

            dateInput.value =
                new Date()
                    .toISOString()
                    .split("T")[0];
        }


    } catch (error) {

        console.error(
            "Failed to initialize dashboard:",
            error
        );

        window.location.replace("login.html");
    }

});


// ==============================
// LOAD TRANSACTIONS
// ==============================

async function loadTransactions() {

    try {

        const response = await fetch(
            `/api/transactions/user/${USER_ID}`
        );

        if (!response.ok) {
            throw new Error("Failed to load transactions");
        }

        const transactions = await response.json();

        displayTransactions(transactions);
updateSummary(transactions);
updateExpenseChart(transactions);

    } catch (error) {

        console.error("Error:", error);

    }
}


// ==============================
// DISPLAY TRANSACTIONS
// ==============================

function displayTransactions(transactions) {

    const transactionList =
        document.getElementById("transactionList");

    transactionList.innerHTML = "";

    if (transactions.length === 0) {

        transactionList.innerHTML =
            "<p class='empty-message'>No transactions yet.</p>";

        return;
    }


    transactions.forEach(transaction => {

        const div = document.createElement("div");

        div.className = "transaction";


        const sign =
            transaction.type === "EXPENSE"
                ? "-"
                : "+";


        const amountClass =
            transaction.type === "EXPENSE"
                ? "amount-expense"
                : "amount-income";


        const typeClass =
            transaction.type === "EXPENSE"
                ? "expense-label"
                : "income-label";


        div.innerHTML = `

            <div class="transaction-info">

                <h4>
                    ${transaction.description}
                </h4>

                <p>
                    ${transaction.category.name}
                    • ${transaction.transactionDate}
                </p>

                <span class="${typeClass}">
                    ${transaction.type}
                </span>

            </div>


            <div class="transaction-actions">

                <span class="${amountClass}">
                    ${sign} ₹${transaction.amount.toFixed(2)}
                </span>

                <button
                    class="edit-btn"
                    onclick="editTransaction(${transaction.id})">
                    Edit
                </button>

                <button
                    class="delete-btn"
                    onclick="deleteTransaction(${transaction.id})">
                    Delete
                </button>

            </div>

        `;


        transactionList.appendChild(div);

    });
}


// ==============================
// UPDATE SUMMARY
// ==============================

function updateSummary(transactions) {

    let income = 0;
    let expenses = 0;


    transactions.forEach(transaction => {

        if (transaction.type === "INCOME") {

            income += transaction.amount;

        } else {

            expenses += transaction.amount;

        }

    });


    const balance = income - expenses;


    document.getElementById("income").textContent =
        `₹${income.toFixed(2)}`;

    document.getElementById("expenses").textContent =
        `₹${expenses.toFixed(2)}`;

    document.getElementById("balance").textContent =
        `₹${balance.toFixed(2)}`;
        // Update expense breakdown
updateExpenseBreakdown(transactions);
}


// ==============================
// LOAD CATEGORIES
// ==============================

async function loadCategories() {

    try {

        const response =
            await fetch("/api/categories");

        const categories =
            await response.json();


        const categorySelect =
            document.getElementById("category");


        categories.forEach(category => {

            const option =
                document.createElement("option");

            option.value = category.id;

            option.textContent = category.name;

            categorySelect.appendChild(option);

        });

    } catch (error) {

        console.error(
            "Error loading categories:",
            error
        );

    }
}
// ==============================
// ADD TRANSACTION
// ==============================

document
    .getElementById("transactionForm")
    .addEventListener("submit", async function(event) {

        event.preventDefault();

        const amount =
            parseFloat(document.getElementById("amount").value);

        const categoryId =
            parseInt(document.getElementById("category").value);

        const type =
            document.getElementById("type").value;

        const description =
            document.getElementById("description").value;

        const transactionDate =
            document.getElementById("date").value;


        if (!categoryId) {
            alert("Please select a category.");
            return;
        }


        const transaction = {

            user: {
                id: USER_ID
            },

            category: {
                id: categoryId
            },

            amount: amount,

            type: type,

            description: description,

            transactionDate: transactionDate
        };


        try {

            const response = await fetch(
                "/api/transactions",
                {
                    method: "POST",

                    headers: {
                        "Content-Type": "application/json"
                    },

                    body: JSON.stringify(transaction)
                }
            );


            if (!response.ok) {
                throw new Error(
                    "Failed to add transaction"
                );
            }


            alert("Transaction added successfully!");


            document
                .getElementById("transactionForm")
                .reset();


            document.getElementById("date").value =
                new Date().toISOString().split("T")[0];


            loadTransactions();


        } catch (error) {

            console.error(error);

            alert(
                "Something went wrong while adding the transaction."
            );

        }

    });
    // ==============================
// DELETE TRANSACTION
// ==============================

async function deleteTransaction(id) {

    const confirmDelete = confirm(
        "Are you sure you want to delete this transaction?"
    );

    if (!confirmDelete) {
        return;
    }

    try {

        const response = await fetch(
            `/api/transactions/${id}`,
            {
                method: "DELETE"
            }
        );

        if (!response.ok) {
            throw new Error("Failed to delete transaction");
        }

        alert("Transaction deleted successfully!");

        // Refresh dashboard
        loadTransactions();

    } catch (error) {

        console.error(error);

        alert(
            "Something went wrong while deleting the transaction."
        );
    }
}
// ==============================
// EDIT TRANSACTION
// ==============================

async function editTransaction(id) {

    try {

        // Get all transactions
        const response = await fetch(
            `/api/transactions/user/${USER_ID}`
        );

        const transactions = await response.json();

        // Find the transaction we want to edit
        const transaction = transactions.find(
            t => t.id === id
        );

        if (!transaction) {
            alert("Transaction not found.");
            return;
        }

        // Ask user for new values
        const newAmount = prompt(
            "Enter new amount:",
            transaction.amount
        );

        if (newAmount === null) {
            return;
        }

        const newDescription = prompt(
            "Enter new description:",
            transaction.description
        );

        if (newDescription === null) {
            return;
        }

        // Create updated transaction
        const updatedTransaction = {

            user: {
                id: USER_ID
            },

            category: {
                id: transaction.category.id
            },

            amount: parseFloat(newAmount),

            type: transaction.type,

            description: newDescription,

            transactionDate: transaction.transactionDate
        };


        // Send PUT request
        const updateResponse = await fetch(
            `/api/transactions/${id}`,
            {
                method: "PUT",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify(updatedTransaction)
            }
        );


        if (!updateResponse.ok) {
            throw new Error(
                "Failed to update transaction"
            );
        }


        alert("Transaction updated successfully!");

        // Refresh dashboard
        loadTransactions();


    } catch (error) {

        console.error(error);

        alert(
            "Something went wrong while updating the transaction."
        );
    }
}
// ==============================
// EXPENSE BREAKDOWN
// ==============================

function updateExpenseBreakdown(transactions) {

    const breakdown =
        document.getElementById("expenseBreakdown");

    const categoryTotals = {};


    transactions.forEach(transaction => {

        if (transaction.type !== "EXPENSE") {
            return;
        }

        const categoryName =
            transaction.category.name;

        if (!categoryTotals[categoryName]) {
            categoryTotals[categoryName] = 0;
        }

        categoryTotals[categoryName] +=
            transaction.amount;
    });


    breakdown.innerHTML = "";


    if (Object.keys(categoryTotals).length === 0) {

        breakdown.innerHTML =
            "<p>No expenses yet.</p>";

        return;
    }


    for (const category in categoryTotals) {

        const amount =
            categoryTotals[category];

        const item =
            document.createElement("p");

        item.textContent =
            `${category} — ₹${amount.toFixed(2)}`;

        breakdown.appendChild(item);
    }
}
// ==============================
// EXPENSE CHART
// ==============================

let expenseChart = null;

function updateExpenseChart(transactions) {

    const categoryTotals = {};

    transactions.forEach(transaction => {

        if (transaction.type !== "EXPENSE") {
            return;
        }

        const categoryName =
            transaction.category.name;

        if (!categoryTotals[categoryName]) {
            categoryTotals[categoryName] = 0;
        }

        categoryTotals[categoryName] +=
            transaction.amount;
    });


    const labels =
        Object.keys(categoryTotals);

    const data =
        Object.values(categoryTotals);


    const canvas =
        document.getElementById("expenseChart");


    if (!canvas) {
        return;
    }


    // Destroy old chart before creating a new one
    if (expenseChart) {
        expenseChart.destroy();
    }


    if (labels.length === 0) {
        return;
    }


    expenseChart = new Chart(canvas, {

        type: "doughnut",

        data: {

            labels: labels,

            datasets: [{
                data: data
            }]
        },

        options: {

            responsive: true,

            plugins: {

                legend: {
                    position: "bottom"
                }

            }

        }

    });
}

// ==============================
// LOGOUT
// ==============================

async function logout() {

    try {

        await fetch(
            "/api/auth/logout",
            {
                method: "POST"
            }
        );

    } catch (error) {

        console.error(
            "Logout error:",
            error
        );

    } finally {

        window.location.replace("login.html");

    }
}