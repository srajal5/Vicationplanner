# 🚀 Vacation Planner - Full-Stack Web Application

A modern, responsive vacation planning application built with Spring Boot backend and React frontend, featuring user authentication, trip planning, booking, and export functionality.

## 📋 Table of Contents

- [Tech Stack](#-tech-stack)
- [Features](#-features)
- [Prerequisites](#-prerequisites)
- [Installation & Setup](#-installation--setup)
- [Project Structure](#-project-structure)
- [Configuration](#-configuration)
- [API Documentation](#-api-documentation)
- [Deployment](#-deployment)
- [Development](#-development)
- [Testing](#-testing)
- [Contributing](#-contributing)

## 🛠 Tech Stack

### Backend (Spring Boot)
- **Framework**: Spring Boot 3.2.0
- **Java Version**: Java 17
- **Database**: MongoDB 4.10.2
- **Build Tool**: Maven
- **Key Dependencies**:
  - `spring-boot-starter-web` - REST API development
  - `spring-boot-starter-data-mongodb` - MongoDB integration
  - `spring-boot-starter-validation` - Request validation
  - `spring-boot-starter-actuator` - Application monitoring
  - `spring-boot-starter-webflux` - Reactive programming support
  - `apache-pdfbox` (2.0.29) - PDF generation
  - `apache-poi` (5.2.4) - Excel file handling
  - `jackson-databind` - JSON processing
  - `httpclient5` (5.2.1) - HTTP client
  - `spring-boot-starter-thymeleaf` - Template engine

### Frontend (React)
- **Framework**: React 18.2.0
- **Build Tool**: Create React App (react-scripts 5.0.1)
- **Routing**: React Router DOM 6.20.1
- **Styling**: Tailwind CSS 3.4.8 with custom animations
- **Authentication**: Clerk React 5.55.0
- **UI Components**:
  - Radix UI (labels, select, slot)
  - Lucide React (icons)
  - Recharts (data visualization)
- **HTTP Client**: Axios 1.6.2
- **Date Handling**: date-fns 2.30.0
- **Utilities**:
  - class-variance-authority (styling variants)
  - clsx (conditional classes)
  - tailwind-merge (class merging)
- **Development Tools**:
  - TypeScript 4.9.5 (type definitions)
  - PostCSS 8.5.6 with Autoprefixer 10.4.21
  - DaisyUI 4.12.0 (component library)
  - ESLint (code linting)

### Additional Libraries & Tools
- **Authentication**: Clerk (user management & authentication)
- **Icons**: Lucide React (modern icon library)
- **Charts**: Recharts (data visualization)
- **Date Pickers**: MUI X Date Pickers (date selection)
- **Material UI**: MUI Core, Icons, and Emotion (UI components)
- **Build Tools**: Maven, npm, PostCSS
- **Code Quality**: PMD (static analysis), ESLint

## ✨ Features

- 🔐 **User Authentication** - Secure sign-in/sign-up with Clerk
- ✈️ **Trip Planning** - Comprehensive trip planning with preferences
- 🏨 **Hotel Booking** - Integrated hotel booking system
- 🎯 **Activity Recommendations** - Personalized activity suggestions
- 💰 **Budget Management** - Cost tracking and budget breakdown
- 📊 **Trip Analytics** - Visual trip data with charts
- 📱 **Responsive Design** - Mobile-first responsive UI
- 🌙 **Dark/Light Theme** - Theme switching capability
- 💾 **Trip Saving** - Save and manage multiple trips
- 📄 **Export Functionality** - PDF and Excel export
- 🗺️ **Map Integration** - Location-based services
- 💱 **Multi-Currency Support** - Currency conversion
- 🔍 **Search & Filter** - Advanced trip search

## 📋 Prerequisites

### System Requirements
- **Java**: JDK 17 or higher
- **Node.js**: v16 or higher
- **npm**: v7 or higher
- **MongoDB**: v4.0 or higher (local or MongoDB Atlas)
- **Git**: Latest version

### Accounts & Services
- **Clerk Account**: For user authentication (get API keys)
- **MongoDB Atlas**: Cloud database (optional, can use local MongoDB)
- **GitHub Account**: For version control and deployment

## 🚀 Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/vacation-planner.git
cd vacation-planner
```

### 2. Backend Setup

#### Install Java Dependencies
```bash
cd backend
# Maven will automatically download dependencies
mvn clean install
```

#### Environment Configuration
Create `.env` file in `backend/` directory:
```env
# MongoDB Configuration
MONGODB_URI=mongodb://localhost:27017/vacation_planner
# Or for MongoDB Atlas:
# MONGODB_URI=mongodb+srv://username:password@cluster.mongodb.net/vacation_planner

# Clerk Authentication
CLERK_SECRET_KEY=your_clerk_secret_key_here

# Server Configuration
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=development

# CORS Configuration
CORS_ALLOWED_ORIGINS=http://localhost:3000,https://your-frontend-domain.com
```

#### Start Backend Server
```bash
# Development mode
mvn spring-boot:run

# Or run the JAR file
mvn clean package
java -jar target/vacation-planner-backend-1.0.0.jar
```

Backend will be available at: `http://localhost:8080`

### 3. Frontend Setup

#### Install Node Dependencies
```bash
cd frontend
npm install
```

#### Environment Configuration
Create `.env` file in `frontend/` directory:
```env
# Clerk Authentication
REACT_APP_CLERK_PUBLISHABLE_KEY=your_clerk_publishable_key_here

# API Configuration
REACT_APP_API_BASE_URL=http://localhost:8080

# Development/Production flags
REACT_APP_ENVIRONMENT=development
```

#### Start Frontend Development Server
```bash
# Build Tailwind CSS
npm run build:tailwind

# Start development server
npm start
```

Frontend will be available at: `http://localhost:3000`

### 4. Database Setup

#### Local MongoDB
```bash
# Install MongoDB locally
# macOS with Homebrew
brew install mongodb-community
brew services start mongodb-community

# Ubuntu/Debian
sudo apt-get install mongodb
sudo systemctl start mongodb

# Windows - Download from mongodb.com
```

#### MongoDB Atlas (Cloud)
1. Create account at mongodb.com/atlas
2. Create a new cluster
3. Get connection string and update `MONGODB_URI` in backend `.env`

## 📁 Project Structure

```
vacation-planner/
├── backend/                          # Spring Boot Backend
│   ├── pom.xml                       # Maven configuration
│   ├── src/main/java/com/vacationplanner/
│   │   ├── VacationPlannerApplication.java    # Main application class
│   │   ├── config/                   # Spring configuration
│   │   ├── controller/               # REST controllers
│   │   │   ├── TripController.java   # Trip management endpoints
│   │   │   ├── BookingController.java # Booking endpoints
│   │   │   └── ExportController.java # Export functionality
│   │   ├── model/                    # Data models
│   │   │   ├── TripPlan.java         # Trip data structure
│   │   │   └── TripPreferences.java  # User preferences
│   │   ├── service/                  # Business logic
│   │   │   ├── TripPlannerService.java    # Trip planning logic
│   │   │   ├── BookingService.java        # Booking logic
│   │   │   ├── DatabaseService.java       # Database operations
│   │   │   ├── ExportService.java         # Export functionality
│   │   │   ├── HotelService.java          # Hotel services
│   │   │   ├── ActivityService.java       # Activity recommendations
│   │   │   ├── MapService.java            # Map integration
│   │   │   ├── FlightService.java         # Flight services
│   │   │   └── AvailabilityService.java   # Availability checking
│   │   └── util/                     # Utilities
│   │       └── EnvLoader.java        # Environment variable loader
│   └── src/main/resources/           # Application resources
│
├── frontend/                         # React Frontend
│   ├── package.json                  # npm configuration
│   ├── tailwind.config.cjs           # Tailwind CSS config
│   ├── postcss.config.cjs            # PostCSS config
│   ├── jsconfig.json                 # JavaScript config
│   ├── public/                       # Static assets
│   │   ├── index.html
│   │   ├── manifest.json
│   │   └── favicon.ico
│   └── src/
│       ├── index.js                  # Application entry point
│       ├── index.css                 # Global styles
│       ├── App.js                    # Main App component
│       ├── components/               # Reusable components
│       │   ├── ui/                   # UI primitives
│       │   │   ├── Button.jsx
│       │   │   ├── Card.jsx
│       │   │   ├── Input.jsx
│       │   │   └── Label.jsx
│       │   ├── Navbar.js             # Navigation bar
│       │   ├── ProfileMenu.js        # User profile menu
│       │   ├── ThemeToggle.js        # Theme switcher
│       │   ├── CurrencySelector.js   # Currency selection
│       │   └── HeroImages.js         # Hero section images
│       ├── pages/                    # Page components
│       │   ├── TripPlanner.js        # Trip planning form
│       │   ├── TripResults.js        # Trip results display
│       │   ├── Booking.js            # Booking page
│       │   └── SavedTrips.js         # Saved trips page
│       ├── contexts/                 # React contexts
│       │   └── ThemeContext.js       # Theme management
│       ├── lib/                      # Utility libraries
│       │   └── utils.js              # Helper functions
│       └── services/                 # API services
│
├── README-MODERN-ARCHITECTURE.md     # This file
├── README.md                         # Original README
└── response.json                     # Sample API response
```

## ⚙️ Configuration

### Backend Configuration
Key configuration files:
- `application.properties` (in resources)
- Environment variables (`.env` file)
- CORS settings in `VacationPlannerApplication.java`

### Frontend Configuration
- `tailwind.config.cjs` - Tailwind CSS configuration
- `postcss.config.cjs` - PostCSS configuration
- Environment variables in `.env`

### Clerk Authentication Setup
1. Create account at clerk.com
2. Create a new application
3. Get publishable and secret keys
4. Configure authorized redirect URLs for your domains

## 📚 API Documentation

### Base URL
```
http://localhost:8080/api
```

### Authentication
All endpoints require Bearer token authentication via Clerk JWT.

### Endpoints

#### Trip Management
```
POST   /api/trips/plan          # Plan a new trip
GET    /api/trips/{id}          # Get trip details
GET    /api/trips               # Get all user trips
POST   /api/trips/{id}/save     # Save a trip
DELETE /api/trips/{id}          # Delete a trip
```

#### Booking
```
POST   /api/booking/book               # Book a trip
GET    /api/booking/availability/{id}  # Check availability
```

#### Export
```
GET    /api/export/pdf/{id}     # Export trip to PDF
GET    /api/export/excel/{id}  # Export trip to Excel
```

### Request/Response Examples

#### Plan Trip Request
```json
{
  "destination": "Paris, France",
  "budget": 2000,
  "currency": "USD",
  "startDate": "2024-06-01",
  "endDate": "2024-06-07",
  "theme": "culture",
  "groupSize": 2,
  "startingPoint": "New York"
}
```

#### Trip Response
```json
{
  "id": "trip_123",
  "destination": "Paris, France",
  "totalCost": 1850,
  "currency": "USD",
  "dailyItinerary": [...],
  "accommodation": {...},
  "transportation": {...},
  "activities": [...],
  "budgetBreakdown": {...}
}
```

## 🚀 Deployment

### Frontend Deployment (Vercel)

1. **Connect Repository**
   ```bash
   # Push frontend code to GitHub
   cd frontend
   git init
   git add .
   git commit -m "Initial commit"
   git remote add origin https://github.com/username/vacation-planner-frontend.git
   git push -u origin main
   ```

2. **Deploy on Vercel**
   - Go to vercel.com and sign in
   - Click "New Project"
   - Import your GitHub repository
   - Configure:
     - Framework: React
     - Root Directory: `./frontend`
     - Build Command: `npm run build`
     - Output Directory: `build`

3. **Environment Variables**
   ```
   REACT_APP_CLERK_PUBLISHABLE_KEY=your_key
   REACT_APP_API_BASE_URL=https://your-backend-url.amazonaws.com
   ```

### Backend Deployment (AWS Elastic Beanstalk)

1. **Build JAR**
   ```bash
   cd backend
   mvn clean package -DskipTests
   ```

2. **Create Elastic Beanstalk Application**
   - Go to AWS Console > Elastic Beanstalk
   - Create application: `vacation-planner-backend`
   - Platform: Java (Corretto 17)
   - Upload JAR file: `target/vacation-planner-backend-1.0.0.jar`

3. **Environment Variables**
   ```
   MONGODB_URI=your_mongodb_connection_string
   CLERK_SECRET_KEY=your_secret_key
   SERVER_PORT=5000
   SPRING_PROFILES_ACTIVE=production
   ```

4. **Configure Security**
   - Set up security groups for database access
   - Configure SSL certificate
   - Set up CloudWatch monitoring

## 💻 Development

### Running in Development
```bash
# Backend
cd backend && mvn spring-boot:run

# Frontend (new terminal)
cd frontend && npm start
```

### Code Quality
```bash
# Backend - Run PMD analysis
cd backend && mvn pmd:check

# Frontend - Run ESLint
cd frontend && npm run lint
```

### Building for Production
```bash
# Backend
cd backend && mvn clean package

# Frontend
cd frontend && npm run build
```

## 🧪 Testing

### Backend Testing
```bash
cd backend
mvn test
```

### Frontend Testing
```bash
cd frontend
npm test
```

### Manual Testing Checklist
- [ ] User registration and login
- [ ] Trip planning form submission
- [ ] Trip results display
- [ ] Booking flow
- [ ] Trip saving and loading
- [ ] Export functionality (PDF/Excel)
- [ ] Responsive design on mobile
- [ ] Theme switching
- [ ] Error handling

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit changes: `git commit -m 'Add your feature'`
4. Push to branch: `git push origin feature/your-feature`
5. Create a Pull Request

### Code Standards
- Follow Java naming conventions for backend
- Use functional components and hooks for React
- Maintain consistent code formatting
- Add comments for complex logic
- Write tests for new features

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support, email support@vacationplanner.com or create an issue in the GitHub repository.

## 🔄 Changelog

### Version 1.0.0
- Initial release with full-stack web application
- User authentication with Clerk
- Trip planning and booking functionality
- Export to PDF and Excel
- Responsive design with Tailwind CSS
- MongoDB integration

---

**Happy Planning! ✈️**
