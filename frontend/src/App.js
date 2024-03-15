import React from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import Login from './pages/login/Login';
import Main from './pages/main/Main';
import LoginContextProvider from './contexts/LoginContextProvider';

function App() {
  return (
    <BrowserRouter>
      <LoginContextProvider>
        <Routes>
            <Route path='/' element={ <Main /> } />
            <Route path="/login" element={ <Login /> }/>    
        </Routes>
      </LoginContextProvider>
    </BrowserRouter>
  );
}

export default App;
