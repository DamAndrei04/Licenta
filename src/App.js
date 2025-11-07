
import './App.css';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Workspace from './Components/Workspace/workspace.jsx';
function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Workspace/>} />
      </Routes>
    </Router>
  );
}

export default App;
