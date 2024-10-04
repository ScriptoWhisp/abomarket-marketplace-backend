# Iti0302 2024 Project
(ChatGPT Ideas for implementation (Not ready yet (DO NOT GRADE (thx))))

---

### **Project Title:** **Online Marketplace Application**

#### **Project Description:**

Develop a full-stack e-commerce web application where users can browse products, add items to a shopping cart, and proceed to checkout. The platform will support user authentication, product management, order processing, and integration with a payment gateway API. This project will provide a comprehensive experience in building a real-world application using modern technologies and best practices.

---

### **Key Features and Requirement Mapping**

#### **1. Database Design**

- **Core Tables:**
  - **Users:** Stores user credentials and profile information.
  - **Products:** Contains product details like name, description, price, and stock.
  - **Orders:** Records purchase orders made by users.
  - **OrderItems:** Links products to orders, including quantity and price at the time of purchase.

- **Additional Tables (Per Teammate):**
  - **Categories:** Classifies products into various categories.
  - **Reviews:** Allows users to leave reviews on products.
  - **ShoppingCart:** Temporarily stores products that users intend to purchase.
  - **Payments:** Logs payment transactions.
  - **Wishlists:** Lets users save products for future reference.

- **Requirement Fulfillment:**
  - **Minimum 3 tables + 1 table per teammate:** With the core and additional tables, you satisfy this requirement.

#### **2. API Endpoints**

- **User Authentication:**
  - **POST /api/auth/signup:** Register a new user.
  - **POST /api/auth/login:** Authenticate user and return JWT.
  - **GET /api/users/profile:** Retrieve user profile details.
  - **PUT /api/users/profile:** Update user profile.

- **Product Management:**
  - **GET /api/products:** List products with search, sorting, and pagination.
  - **GET /api/products/{id}:** Get product details by ID.
  - **POST /api/products:** Add a new product (admin only).
  - **PUT /api/products/{id}:** Update product details (admin only).
  - **DELETE /api/products/{id}:** Delete a product (admin only).

- **Shopping Cart:**
  - **GET /api/cart:** Retrieve current user's shopping cart.
  - **POST /api/cart:** Add product to cart.
  - **PUT /api/cart/{itemId}:** Update cart item quantity.
  - **DELETE /api/cart/{itemId}:** Remove item from cart.

- **Order Processing:**
  - **POST /api/orders:** Create a new order.
  - **GET /api/orders:** List orders for the authenticated user.
  - **GET /api/orders/{id}:** Get order details.

- **Reviews:**
  - **POST /api/products/{id}/reviews:** Add a review for a product.
  - **GET /api/products/{id}/reviews:** Get reviews for a product.

- **Integration with Payment API:**
  - **POST /api/payments:** Process payments via external API (e.g., Stripe).

- **Requirement Fulfillment:**
  - **Minimum 10 endpoints + 2 endpoints per teammate:** The above endpoints fulfill this criterion.

#### **3. Frontend Functionality**

- **Home Page:**
  - Display featured products and categories.
- **Product Listing Page:**
  - Show products with sorting (by price, popularity), search functionality, and pagination.
- **Product Detail Page:**
  - Display detailed information, including reviews.
- **Shopping Cart:**
  - Allow users to view and manage cart items.
- **Checkout Process:**
  - Collect shipping information and process payments.
- **User Account Pages:**
  - Profile management, order history, wishlist.

- **Requirement Fulfillment:**
  - **Table view with sorting, search, and pagination:** Implemented in the product listing page.

#### **4. Security and Authentication**

- **User Authentication:**
  - Implemented using **Spring Security** and **JWT** tokens.
- **Role-Based Access Control:**
  - Differentiate between regular users and admin roles.

- **Requirement Fulfillment:**
  - **Login functionality using Spring Security + JWT:** Covered by the authentication endpoints.

#### **5. External API Integration**

- **Payment Processing:**
  - Integrate with **Stripe API** to handle payment transactions securely.
- **Optional Integration:**
  - Use a shipping API for order tracking or a product recommendation API.

- **Requirement Fulfillment:**
  - **Integration to other API:** Achieved through payment processing.

#### **6. Documentation**

- **OpenAPI Documentation:**
  - Use **Swagger** or **SpringDoc OpenAPI** to document all APIs.

- **GitLab Wiki Pages:**
  - **Database Diagram:** Visual representation of your database schema.
  - **Project Description:** Overview, objectives, and features.
  - **Component Diagram:** Show the architecture and components.
  - **Installation Guide:** Detailed steps to set up the project, including stack and commands.

- **READMEs:**
  - Provide clear instructions for both frontend and backend projects.

- **Requirement Fulfillment:**
  - **4 wiki pages** and **2 READMEs** as per the requirements.

#### **7. Code Quality and Best Practices**

- **DTOs Usage:**
  - Use Data Transfer Objects in controllers and frontend, ensuring they are case-specific.
- **MapStruct:**
  - Utilize **MapStruct** for mapping between entities and DTOs.
- **Lombok:**
  - Employ **Lombok** annotations for getters, setters, and constructors.
- **SLF4J Logging:**
  - Implement logging using **SLF4J**.
- **SonarLint Compliance:**
  - Ensure no critical warnings are present.
- **Error Handling:**
  - Implement global exception handling using `@ControllerAdvice`.
- **RESTful API Design:**
  - Follow best practices for URL naming and HTTP methods.

- **Requirement Fulfillment:**
  - Addresses all coding standards and practices specified.

#### **8. Deployment and CI/CD**

- **GitLab CI/CD Pipelines:**
  - Set up pipelines for both frontend and backend with automated testing and deployment.
- **Docker Deployment:**
  - Containerize the backend using Docker.
  - Optionally use **docker-compose** for orchestrating multiple containers.
- **Server Configuration:**
  - Externalize `application.properties` on the server.
- **Frontend Accessibility:**
  - Serve frontend via **Nginx** on port 80 and make it accessible via a hostname.

- **Requirement Fulfillment:**
  - All server and CI/CD deployment requirements are satisfied.

#### **9. Testing**

- **Unit Tests:**
  - Write unit tests for the service layer to achieve at least **80% code coverage**.
- **Testing Tools:**
  - Use **JUnit** and **Mockito** for testing.

- **Requirement Fulfillment:**
  - **Unit tests | Code coverage 80% service layer:** Achieved through comprehensive testing.

---

### **Technical Stack**

#### **Backend:**

- **Language:** Java
- **Framework:** Spring Boot
- **Database:** MySQL or PostgreSQL
- **ORM:** Hibernate (via Spring Data JPA)
- **Security:** Spring Security with JWT
- **Mapping:** MapStruct
- **Logging:** SLF4J with Logback
- **Testing:** JUnit, Mockito
- **Build Tool:** Maven or Gradle

#### **Frontend:**

- **Framework:** React, Angular, or Vue.js (choose based on team preference)
- **State Management:** Redux (for React), NgRx (for Angular), or Vuex (for Vue.js)
- **Routing:** React Router, Angular Router, or Vue Router
- **HTTP Client:** Axios or Fetch API
- **UI Library:** Material-UI, Bootstrap, or Ant Design

#### **CI/CD and Deployment:**

- **CI/CD:** GitLab CI/CD
- **Containerization:** Docker
- **Web Server:** Nginx
- **Hosting:** A server accessible to your team (could be AWS, DigitalOcean, etc.)

---

### **Implementation Plan**

#### **1. Project Setup**

- **Initialize Repositories:**
  - Set up separate GitLab repositories for frontend and backend.
- **Configure CI/CD Pipelines:**
  - Create `.gitlab-ci.yml` files for both projects.
- **Set Up Docker:**
  - Write `Dockerfile` for the backend.
  - Optionally create `docker-compose.yml` if using multiple services.

#### **2. Backend Development**

- **Authentication Module:**
  - Implement user registration and login with JWT.
- **Product Module:**
  - Develop CRUD operations for products.
- **Cart and Order Modules:**
  - Implement shopping cart functionality.
  - Handle order creation and processing.
- **Payment Integration:**
  - Integrate with Stripe API for payment processing.
- **Review Module:**
  - Allow users to add and view product reviews.
- **Exception Handling:**
  - Implement global exception handling.

#### **3. Frontend Development**

- **User Interface:**
  - Design UI components for all pages.
- **State Management:**
  - Manage application state using the chosen state management library.
- **API Integration:**
  - Connect frontend to backend APIs.
- **Routing:**
  - Set up client-side routing for navigation.
- **Authentication Flow:**
  - Implement login and signup forms.
  - Secure routes that require authentication.

#### **4. Testing**

- **Backend Testing:**
  - Write unit tests for services and controllers.
- **Frontend Testing:**
  - Write tests for components and utilities (optional but recommended).
- **Code Coverage:**
  - Use tools like **JaCoCo** for backend and **Jest** for frontend to measure coverage.

#### **5. Documentation**

- **API Documentation:**
  - Annotate backend code with Swagger/OpenAPI annotations.
- **Wiki Pages:**
  - Collaborate to create the required wiki pages.
- **READMEs:**
  - Provide clear setup and usage instructions.

#### **6. Deployment**

- **Backend Deployment:**
  - Deploy Docker container on the server.
- **Frontend Deployment:**
  - Build the frontend and serve it via Nginx.
- **Environment Configuration:**
  - Use external `application.properties` and environment variables for sensitive data.

#### **7. Team Collaboration**

- **Task Allocation:**
  - Distribute tasks according to team members' strengths.
- **Regular Meetings:**
  - Schedule stand-ups or weekly meetings to track progress.
- **Code Reviews:**
  - Implement a peer-review process before merging code.

---

### **Additional Features (Optional)**

- **Email Notifications:**
  - Send order confirmation emails using services like SendGrid.
- **Admin Dashboard:**
  - Create an interface for admins to manage products and orders.
- **Inventory Management:**
  - Implement stock level checks and notifications for low stock.
- **Search Engine Optimization (SEO):**
  - Optimize frontend for better search engine ranking.
- **Analytics:**
  - Integrate Google Analytics to track user behavior.

---

### **Potential Challenges and Solutions**

- **Concurrency Issues:**
  - **Challenge:** Handling simultaneous updates to product stock.
  - **Solution:** Use database transactions and locking mechanisms.
- **Security Risks:**
  - **Challenge:** Protecting sensitive user data.
  - **Solution:** Implement strong encryption, validate inputs, and follow security best practices.
- **Payment Compliance:**
  - **Challenge:** Meeting PCI DSS compliance.
  - **Solution:** Use trusted payment gateways that handle sensitive data.

---

### **Final Checklist**

- **All Required Features Implemented**
- **Documentation Complete**
- **CI/CD Pipelines Functional**
- **Code Meets Quality Standards**
- **Application Deployed and Accessible**
- **Team Contributions Evident**

---

### **Conclusion**

By choosing to develop an e-commerce application, you can effectively meet all the project requirements while working on a practical and widely applicable project. This will not only fulfill your assignment criteria but also provide a valuable addition to your professional portfolio.

---

If you need further assistance with specific aspects of the project, such as setting up CI/CD pipelines, integrating the payment API, or any other component, feel free to ask!