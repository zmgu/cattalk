import './Main.css';
import MenuBar from '../../components/menuBar/MenuBar';
import ChatingRoomList from './roomList/ChatingRoomList'
import FriendsList from './friendsList/FriendsList';
import { Routes, Route } from 'react-router-dom';

const Main = () => {
    return (
        <div className='main-container'>
            <Routes>
                <Route path='/' element={ <ChatingRoomList /> } />
                <Route path='/friendsList' element={ <FriendsList /> } />
            </Routes>
            
            <MenuBar />

        </div>
    );
}

export default Main;