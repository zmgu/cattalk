import './MenuBar.css'
import { Link } from 'react-router-dom'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faUser, faComment, faEllipsis } from '@fortawesome/free-solid-svg-icons';

const MenuBar = () => {
    return (
        <div className='menubar-container'>
            <div className='menu-link-box'>
                <Link to="/friends" className='menu-link'><FontAwesomeIcon icon={faUser} className='menubar-icon'/></Link>
                <Link to="" className='menu-link'><FontAwesomeIcon icon={faComment} className='menubar-icon'/></Link>
                <Link to="" className='menu-link'><FontAwesomeIcon icon={faEllipsis} className='menubar-icon'/></Link>
            </div>
        </div>
    );

}

export default MenuBar;