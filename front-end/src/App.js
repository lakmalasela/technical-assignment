
import "./App.css";
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import CustomerManager from './pages/CustomerManager';

function App() {

   return (
    <Router>
      <Routes>
        <Route path="/" element={<Navigate to="/customers" />} />
        <Route path="/customers" element={<CustomerManager />} />
      </Routes>
    </Router>
  );
}

export default App;
