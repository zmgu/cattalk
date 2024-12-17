import React, { useState } from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import Login from './pages/login/Login';
import Main from './pages/main/Main';
import LoginContextProvider from './contexts/LoginContextProvider';
import OAuth2RedirectHandler from './contexts/OAuth2RedirectHandler.jsx';
import Chat from './pages/chating/Chat.jsx';
import Modal from './components/modal/Modal.jsx'
import { ModalContext } from './contexts/ModalContext';

function App() {
  const [showModal, setShowModal] = useState(false);

  const openModal = () => setShowModal(true);
  const closeModal = () => setShowModal(false);

  return (
    <BrowserRouter>
      <LoginContextProvider>
        <ModalContext.Provider value={{ openModal, closeModal }}>
          <Routes>
              <Route path='/oauth2' element={<OAuth2RedirectHandler />} />  
              <Route path='*' element={<Main />} />
              <Route path="/login" element={<Login />} />
              <Route path='/chat/:roomId' element={<Chat />} />
          </Routes>
          {showModal && <Modal onClose={closeModal} />}
        </ModalContext.Provider>
      </LoginContextProvider>
    </BrowserRouter>
  );
}

export default App;
