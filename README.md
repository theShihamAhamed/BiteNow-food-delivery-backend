# 🍔 BiteNow — Food Delivery Backend API

A RESTful backend API for the **BiteNow** online food delivery application, built with Spring Boot. This project was developed to gain hands-on experience with Spring Boot, Spring Data MongoDB, Spring Security, and third-party integrations including AWS S3 and Stripe payments.

---

## 📌 About

BiteNow is a full-featured food delivery REST API that handles user authentication, food catalogue management, shopping cart operations, and order processing with real payment integration. The backend is stateless, secured with JWT, and connects to MongoDB as its primary data store.

---

## 🚀 Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.5.9 |
| Language | Java 17 |
| Database | MongoDB (Spring Data MongoDB) |
| Security | Spring Security + JWT (jjwt 0.11.5) |
| File Storage | AWS S3 (AWS SDK v2 2.40.17) |
| Payments | Stripe Java SDK 24.10.0 |
| Boilerplate Reduction | Lombok 1.18.36 |
| Serialisation | Jackson, Gson 2.10.1 |
| Build Tool | Maven |

---

## 🗂️ Project Structure

```
src/main/java/com/dev/BiteNowAPI/
├── BiteNowApiApplication.java       # Application entry point
│
├── config/
│   ├── AWSConfig.java               # AWS S3 client bean
│   └── SecurityConfig.java          # Spring Security + CORS configuration
│
├── controller/
│   ├── AuthController.java          # Login endpoint
│   ├── UserController.java          # User registration
│   ├── FoodController.java          # Food CRUD + image upload
│   ├── CartController.java          # Cart management
│   ├── OrderController.java         # Order creation + Stripe checkout
│   └── StripeWebhookController.java # Stripe payment webhook handler
│
├── entity/
│   ├── UserEntity.java              # MongoDB users collection
│   ├── FoodEntity.java              # MongoDB foods collection
│   ├── CartEntity.java              # MongoDB carts collection
│   └── OrderEntity.java             # MongoDB orders collection
│
├── filters/
│   └── JwtAuthenticationFilter.java # Per-request JWT validation filter
│
├── io/                              # DTOs (Request / Response objects)
│   ├── AuthenticationRequest.java
│   ├── AuthenticationResponse.java
│   ├── UserRequest.java / UserResponse.java
│   ├── FoodRequest.java / FoodResponse.java
│   ├── CartRequest.java / CartResponse.java
│   ├── OrderRequest.java / OrderResponse.java
│   └── OrderItem.java
│
├── repository/
│   ├── UserRepository.java
│   ├── FoodRepository.java
│   ├── CartRepository.java
│   └── OrderRepository.java
│
├── service/
│   ├── AppUserDetailsService.java       # UserDetailsService implementation
│   ├── AuthenticationFacade.java        # Interface for SecurityContext access
│   ├── AuthenticationFacadeImpl.java
│   ├── UserService.java / UserServiceImpl.java
│   ├── FoodService.java / FoodServiceImpl.java
│   ├── CartService.java / CartServiceImpl.java
│   └── OrderService.java / OrderServiceImpl.java
│
└── util/
    └── JwtUtil.java                 # JWT generation & validation helpers
```

---

## 📡 API Endpoints

### Authentication — Public

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `POST` | `/api/register` | Register a new user | ❌ None |
| `POST` | `/api/login` | Login and receive a JWT | ❌ None |

**Register — Request Body**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "secret123"
}
```

**Login — Request Body**
```json
{
  "email": "john@example.com",
  "password": "secret123"
}
```

**Login — Response**
```json
{
  "email": "john@example.com",
  "token": "<JWT>"
}
```

---

### Food Management — Public (read) / Protected (write & delete)

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `GET` | `/api/foods` | Retrieve all food items | ❌ None |
| `GET` | `/api/foods/{id}` | Retrieve a single food item | ❌ None |
| `POST` | `/api/foods` | Add a new food item with image | ✅ JWT |
| `DELETE` | `/api/foods/{id}` | Delete a food item | ✅ JWT |

The `POST /api/foods` endpoint accepts a **multipart/form-data** request:
- `food` — JSON string with name, description, price, and category
- `file` — image file (uploaded to AWS S3)

---

### Cart Management — Protected

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `GET` | `/api/cart` | Get the current user's cart | ✅ JWT |
| `POST` | `/api/cart` | Add an item to cart | ✅ JWT |
| `POST` | `/api/cart/remove` | Remove one item from cart | ✅ JWT |
| `DELETE` | `/api/cart` | Clear the entire cart | ✅ JWT |

**Add/Remove — Request Body**
```json
{
  "foodId": "<food_document_id>"
}
```

---

### Orders & Payments — Protected

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `POST` | `/api/orders/create` | Create order + get Stripe checkout URL | ✅ JWT |
| `POST` | `/api/webhook/stripe` | Receive Stripe payment events | ❌ None (Stripe) |

**Create Order — Request Body**
```json
{
  "orderItems": [
    {
      "foodID": "<id>",
      "name": "Burger",
      "quantity": 2,
      "price": 850.00,
      "category": "Fast Food",
      "imageUrl": "https://...",
      "description": "Juicy beef burger"
    }
  ],
  "phoneNumber": "0771234567",
  "email": "john@example.com",
  "userAddress": "123 Main St, Colombo",
  "amount": 1700.00
}
```

**Create Order — Response** (includes Stripe checkout URL)
```json
{
  "id": "<order_id>",
  "userAddress": "123 Main St, Colombo",
  "phoneNumber": "0771234567",
  "email": "john@example.com",
  "amount": 1700.00,
  "paymentStatus": "PENDING",
  "orderStatus": "CREATED",
  "stripeOrderId": "<stripe_session_id>",
  "checkoutUrl": "https://checkout.stripe.com/..."
}
```

---

## 🔐 Security

- **Stateless JWT authentication** — no server-side sessions.
- Tokens are signed with **HS256** and expire after **7 days**.
- Passwords are hashed with **BCrypt** before storage.
- The `JwtAuthenticationFilter` intercepts every request, extracts the Bearer token from the `Authorization` header, validates it, and populates the `SecurityContext`.
- **CORS** is configured to allow requests from `http://localhost:5173` and `http://localhost:5174` (typical Vite/React dev ports).

**Public routes** (no token required):
```
POST  /api/register
POST  /api/login
GET   /api/foods/**
POST  /api/webhook/stripe
```

All other routes require a valid `Authorization: Bearer <token>` header.

---

## 🗄️ MongoDB Collections

| Collection | Document | Description |
|---|---|---|
| `users` | `UserEntity` | Stores user credentials and profile |
| `foods` | `FoodEntity` | Food item catalogue with S3 image URLs |
| `carts` | `CartEntity` | Per-user cart (Map of foodId → quantity) |
| `orders` | `OrderEntity` | Order records with Stripe session linkage |

---

## 💳 Payment Flow (Stripe)

1. Client calls `POST /api/orders/create` with order details.
2. The API saves the order with `paymentStatus: PENDING` and `orderStatus: CREATED`.
3. A **Stripe Checkout Session** is created and the session URL is returned to the client.
4. The user completes payment on the Stripe-hosted page.
5. Stripe sends a `checkout.session.completed` event to `POST /api/webhook/stripe`.
6. The API verifies the webhook signature, updates the order to `paymentStatus: PAID` and `orderStatus: CONFIRMED`, and clears the user's cart.

---

## ☁️ AWS S3 — Image Storage

Food images are uploaded to an S3 bucket (`amzn-s3-bitenow`) with `public-read` ACL. Each image is stored under a UUID-based key. The public URL is persisted in the `FoodEntity.imageUrl` field. On food deletion, the image is also removed from S3.

---

## ⚙️ Environment Variables

All sensitive configuration is externalised through environment variables. **Never commit real secrets to version control.**

| Variable | Description |
|---|---|
| `MONGODB_URI` | MongoDB connection string |
| `AWS_ACCESS_KEY` | AWS IAM access key ID |
| `AWS_SECRET_KEY` | AWS IAM secret access key |
| `AWS_REGION` | AWS region (e.g. `ap-south-1`) |
| `JWT_SECRET_KEY` | Secret used to sign JWT tokens |
| `STRIPE_SECRET_KEY` | Stripe secret API key |
| `STRIPE_WEBHOOK_SECRET` | Stripe webhook signing secret |

---

## 🛠️ Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- A running MongoDB instance (local or Atlas)
- AWS account with an S3 bucket named `amzn-s3-bitenow`
- Stripe account (test mode is fine)

### 1. Clone the repository

```bash
git clone https://github.com/theShihamAhamed/BiteNow-food-delivery-backend.git
cd BiteNow-food-delivery-backend
```

### 2. Set environment variables

Create a `.env` file or export the variables in your shell:

```bash
export MONGODB_URI="mongodb+srv://<user>:<password>@cluster.mongodb.net/bitenow"
export AWS_ACCESS_KEY="your-access-key"
export AWS_SECRET_KEY="your-secret-key"
export AWS_REGION="ap-south-1"
export JWT_SECRET_KEY="your-very-long-random-secret"
export STRIPE_SECRET_KEY="sk_test_..."
export STRIPE_WEBHOOK_SECRET="whsec_..."
```

### 3. Build and run

```bash
./mvnw spring-boot:run
```

The server starts on **port 8081** by default.

### 4. Test the API

You can use [Postman](https://www.postman.com/) or any REST client:

```bash
# Register
curl -X POST http://localhost:8081/api/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Test User","email":"test@example.com","password":"pass123"}'

# Login and grab the token
curl -X POST http://localhost:8081/api/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"pass123"}'
```

### 5. Configure Stripe webhooks (local testing)

Install the [Stripe CLI](https://stripe.com/docs/stripe-cli) and forward events to your local server:

```bash
stripe listen --forward-to localhost:8081/api/webhook/stripe
```

---

## 🧑‍💻 What I Learned

This project was built to gain practical experience with:

- **Spring Boot** — project scaffolding, auto-configuration, and the request lifecycle
- **Spring Data MongoDB** — repository pattern, custom query methods (`findByEmail`, `findByUserId`, `findByStripeOrderId`)
- **Spring Security** — filter chain configuration, stateless JWT auth, `UserDetailsService`, `AuthenticationManager`, `PasswordEncoder`
- **JWT** — token generation, signing, parsing, and per-request validation via an `OncePerRequestFilter`
- **AWS SDK v2** — programmatic S3 uploads and deletions using `S3Client`
- **Stripe Java SDK** — creating Checkout Sessions and verifying webhook events with signature validation
- **Layered architecture** — separating concerns across controllers, services (interface + impl), repositories, entities, and DTOs
- **Multipart file handling** — accepting both JSON metadata and a file upload in a single `POST` request

---

## 📄 License

This project is for educational purposes. No license has been specified.
