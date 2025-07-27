## JSON Request Bodies for LMS Application

### 🏢 Tenants

* **Method:** `POST`
* **Endpoint:** `/lms/tenants`

```json
{
  "name": "Global Tech University",
  "isActive": true
}
```

---

### 👤 Users

* **Method:** `POST`
* **Endpoint:** `/lms/users`

```json
{
  "username": "vinn",
  "password": "securePassword123",
  "email": "vinn@gmail.com",
  "name": "Vinn",
  "roleId": 2,
  "address": "No. 441, Zarni (12) Street, Soutk Okkalapa, Yangon",
  "phoneNumber": "123-456-7890",
  "tenantId": 1
}
```

---

### 🗂️ Categories

* **Method:** `POST`
* **Endpoint:** `/lms/categories`

```json
{
  "name": "Computer Science",
  "description": "Courses related to programming, algorithms, and data structures.",
  "tenantId": 1
}
```

---

### 📚 Courses

* **Method:** `POST`
* **Endpoint:** `/lms/courses`

```json
{
  "title": "Introduction to Java Programming",
  "description": "A beginner-friendly course on the fundamentals of Java.",
  "categoryId": 1,
  "instructorId": 1,
  "durationDayCount": 90
}
```

---

### 🧩 Modules

* **Method:** `POST`
* **Endpoint:** `/lms/modules`

```json
{
  "name": "Week 1: Getting Started",
  "description": "Introduction to the development environment and basic syntax.",
  "courseId": 1
}
```

---

### 📖 Lessons

* **Method:** `POST`
* **Endpoint:** `/lms/lessons`

```json
{
  "title": "Setting up your IDE",
  "content": "This lesson walks you through installing and configuring IntelliJ IDEA.",
  "materialType": "Video",
  "moduleId": 1
}
```

---

### ❓ Quiz

* **Method:** `POST`
* **Endpoint:** `/lms/quiz`

```json
{
  "moduleId": 1,
  "title": "Week 1 Knowledge Check",
  "questions": [
    {
      "question": "What is the main method signature in Java?",
      "answerOptions": [
        {
          "answer": "public static void main(String[] args)",
          "isCorrect": true
        },
        {
          "answer": "public void main(String[] args)",
          "isCorrect": false
        },
        {
          "answer": "static void main()",
          "isCorrect": false
        }
      ]
    },
    {
      "question": "Which keyword is used to define a constant in Java?",
      "answerOptions": [
        {
          "answer": "const",
          "isCorrect": false
        },
        {
          "answer": "final",
          "isCorrect": true
        },
        {
          "answer": "static",
          "isCorrect": false
        }
      ]
    }
  ]
}
```