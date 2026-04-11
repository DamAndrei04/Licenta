
import './App.css';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
//import Workspace from './components/Workspace/workspace.jsx';
import WorkspacePage from './pages/WorkspacePage';
import RegisterForm from "@/features/registerPage/RegisterForm";
import LoginForm from "@/features/loginPage/LoginForm";
import LandingPage from "@/features/landingPage/LandingPage";
import Dashboard from "@/features/dashboard/Dashboard";
function App() {
  return (
    <Router>
      <Routes>
          <Route path="/" element={<LandingPage />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/register" element={<RegisterForm />} />
          <Route path="/login" element={<LoginForm />} />
          <Route path="/workspace" element={<WorkspacePage/>} />
      </Routes>
    </Router>
  );
}

export default App;
