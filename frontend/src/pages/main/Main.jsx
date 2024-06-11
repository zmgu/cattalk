import './Main.css';
import MenuBar from '../../components/menuBar/MenuBar';
import ChatingRoomList from './roomList/ChatingRoomList'
import FriendsListPage from './friendsList/FriendsListPage';
import { Routes, Route } from 'react-router-dom';

const Main = () => {
    return (
        <div className='main-container'>
            <Routes>
                <Route path='/' element={ <ChatingRoomList /> } />
                <Route path='/friends' element={ <FriendsListPage /> } />
            </Routes>
            
            <MenuBar />

        </div>
    );
}

export default Main;