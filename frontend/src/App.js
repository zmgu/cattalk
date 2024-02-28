import React from 'react';
import { BrowserRouter as Router } from 'react-router-dom'; // BrowserRouter를 사용합니다.
import Login from './pages/login/Login';

function App() {
  return (
    <Router> {/* Router를 추가합니다. */}
      <Login />
    </Router>
  );
}

export default App;
