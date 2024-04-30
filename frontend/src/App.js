import React from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import Login from './pages/login/Login';
import Main from './pages/main/Main';
import LoginContextProvider from './contexts/LoginContextProvider';
import OAuth2RedirectHandler from './contexts/OAuth2RedirectHandler.jsx';
import Chat from './pages/chating/Chat.jsx';

function App() {
  return (
    <BrowserRouter>
      <LoginContextProvider>
        <Routes>
            <Route path='/oauth2' element={<OAuth2RedirectHandler />} />  
            <Route path='*' element={ <Main /> } />
            <Route path="/login" element={ <Login /> }/>
            <Route path='/chat/:roomId' element={ <Chat /> } />
        </Routes>
      </LoginContextProvider>
    </BrowserRouter>
  );
}
export default App;
