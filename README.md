# GRWM - Get Ready With Me

A full-stack application that provides personalized style recommendations based on weather conditions and user preferences. GRWM combines a Spring Boot backend API with a React frontend to deliver intelligent outfit suggestions.

## 🌟 Features

- **User Authentication**: Secure JWT-based authentication system
- **Weather Integration**: Real-time weather data integration for location-based recommendations
- **Style Recommendations**: AI-powered outfit suggestions based on weather, season, and user preferences
- **User Management**: Complete user profile management and preferences
- **RESTful API**: Fully documented REST API endpoints
- **Responsive Frontend**: React-based UI for seamless user experience

## 🏗️ Architecture

### Backend
- **Framework**: Spring Boot 4.0.2
- **Database**: PostgreSQL
- **Authentication**: JWT (JSON Web Tokens)
- **ORM**: JPA/Hibernate
- **Java Version**: Java 25

### Frontend
- **Library**: React
- **Build System**: Node.js based
- **Components**: Custom React components
- **Styling**: CSS-based styling system

## 📋 Prerequisites

### Backend
- Java 25 or higher
- Maven 3.6+
- PostgreSQL 12+

### Frontend
- Node.js 16+ (optional, for development)
- npm or yarn

## 🚀 Quick Start

### Backend Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd GRWM-main
   ```

2. **Configure Database**
   
   Create `src/main/resources/application.yml` with the following configuration:
   ```yaml
   spring:
     application:
       name: grwm
     datasource:
       url: jdbc:postgresql://localhost:5432/grwm_db
       username: postgres
       password: your_password
       driver-class-name: org.postgresql.Driver
     jpa:
       hibernate:
         ddl-auto: update
       show-sql: false
       properties:
         hibernate:
           dialect: org.hibernate.dialect.PostgreSQLDialect
   ```

3. **Build the Backend**
   ```bash
   ./mvnw clean install
   ```

4. **Run the Application**
   ```bash
   ./mvnw spring-boot:run
   ```

   The backend will start on `http://localhost:8080`

### Frontend Setup

1. **Navigate to Frontend Directory**
   ```bash
   cd grwm-frontend
   ```

2. **Install Dependencies**
   ```bash
   npm install
   ```

3. **Run Development Server**
   ```bash
   npm start
   ```

   The frontend will be available at `http://localhost:3000`

## 📁 Project Structure

```
GRWM-main/
├── src/
│   ├── main/
│   │   ├── java/com/wthxrsh/grwm/
│   │   │   ├── controller/           # REST API Controllers
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── UserController.java
│   │   │   │   └── RecommendationController.java
│   │   │   ├── model/                # JPA Entity Models
│   │   │   │   ├── User.java
│   │   │   │   └── StyleRecommendation.java
│   │   │   ├── repository/           # Data Access Layer
│   │   │   ├── service/              # Business Logic
│   │   │   │   ├── AIService.java
│   │   │   │   ├── RecommendationService.java
│   │   │   │   ├── JwtService.java
│   │   │   │   └── WeatherService.java
│   │   │   ├── security/             # Security Configuration
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── config/               # Application Configuration
│   │   │   ├── exception/            # Exception Handling
│   │   │   └── GrwmApplication.java  # Main Application Class
│   │   └── resources/
│   │       └── application.yml       # Application Configuration
│   └── test/                         # Unit Tests
├── grwm-frontend/                    # React Frontend
│   ├── src/
│   │   ├── Components/               # React Components
│   │   ├── pages/                    # Page Components
│   │   └── styles/                   # CSS Stylesheets
│   └── package.json
├── pom.xml                           # Maven Configuration
├── mvnw                              # Maven Wrapper (Unix/Linux)
├── mvnw.cmd                          # Maven Wrapper (Windows)
└── README.md                         # This File
```

## 🔌 API Endpoints

### Authentication Endpoints
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh` - Refresh JWT token

### User Endpoints
- `GET /api/users/profile` - Get user profile
- `PUT /api/users/profile` - Update user profile
- `DELETE /api/users/{id}` - Delete user account

### Recommendation Endpoints
- `POST /api/recommendations/suggest` - Get style recommendations
- `GET /api/recommendations/history` - Get recommendation history
- `GET /api/recommendations/{id}` - Get specific recommendation

## 🔐 Security

- **JWT Authentication**: Token-based authentication for secure API access
- **Password Encryption**: Passwords are securely hashed using Spring Security
- **CORS Configuration**: Configured to accept requests from allowed origins
- **Authorization Filters**: JWT validation on protected endpoints

## 🛠️ Technologies Used

### Backend
- Spring Boot 4.0.2
- Spring Security
- Spring Data JPA
- Spring Validation
- PostgreSQL JDBC Driver
- Lombok
- Maven

### Frontend
- React
- CSS3
- JavaScript (ES6+)

## 📝 Development

### Building the Project

```bash
# Full build
./mvnw clean install

# Compile only
./mvnw clean compile

# Run tests
./mvnw test

# Package as JAR
./mvnw clean package
```

### Code Quality

The project follows standard Java and React best practices:
- Clean code principles
- MVC architecture pattern
- Repository pattern for data access
- Service layer for business logic
- Component-based UI structure

## 🐛 Troubleshooting

### Build Issues
- Ensure Java 25 is installed: `java -version`
- Clear Maven cache: `./mvnw clean`
- Verify Maven configuration: `./mvnw -v`

### Database Connection Issues
- Verify PostgreSQL is running
- Check database credentials in `application.yml`
- Ensure database `grwm_db` is created
- Check PostgreSQL port (default: 5432)

### Frontend Issues
- Clear npm cache: `npm cache clean --force`
- Reinstall dependencies: `rm -rf node_modules && npm install`
- Check Node.js version: `node -v` (should be 16+)

## 🤝 Contributing

1. Create a feature branch: `git checkout -b feature/your-feature`
2. Commit changes: `git commit -am 'Add new feature'`
3. Push to branch: `git push origin feature/your-feature`
4. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👤 Author

- **wthxrsh** - Project Lead and Developer

## 📞 Support

For support and questions, please open an issue in the repository or contact the development team.

## 🗺️ Roadmap

- [ ] Advanced AI recommendations using ML models
- [ ] Social sharing features
- [ ] Mobile app (iOS/Android)
- [ ] Multi-language support
- [ ] Integration with fashion APIs
- [ ] Real-time notifications
- [ ] Advanced analytics dashboard

## 🔄 Version History

### v0.0.1-SNAPSHOT
- Initial project setup
- User authentication system
- Basic style recommendation engine
- Weather integration
- Frontend scaffolding

---

**Last Updated**: August 2026

Happy Styling! 🎨👗👠
