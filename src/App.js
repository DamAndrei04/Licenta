
import './App.css';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
//import Workspace from './components/Workspace/workspace.jsx';
import WorkspacePage from './pages/WorkspacePage';
function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<WorkspacePage/>} />
      </Routes>
    </Router>
  );
}

export default App;
