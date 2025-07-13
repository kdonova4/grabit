import { Link } from "react-router-dom";
import { useAuth } from "./AuthContext"

import { Container, Nav, Navbar } from "react-bootstrap";

const NavBar: React.FC = () => {

    const { token, logout } = useAuth();

    return(
        <>
            <Navbar>
                <Container>
                    <Nav.Link as={Link} to={"/"}>
                        <img
                        src="/icon_162460_256.jpg"
                        alt="Brand Logo"
                        height="80"
                        className=""/>
                    </Nav.Link>
                    <div className="product-search">
                        Product Search Bar{/* Product Search Component Here */}
                    </div>
                    
                    {token ? (
                        <div className="profile-link">
                            <Nav.Link as={Link} to={"/profile"}>Profile</Nav.Link>
                            <Nav.Link onClick={logout}>Logout</Nav.Link>
                        </div>
                    ) : (
                        <div className="profile-link">
                            <Nav.Link as={Link} to={"login"}>Login</Nav.Link>
                            <Nav.Link as={Link} to={"/register"}>Register</Nav.Link>
                        </div>
                    )}

                </Container>
            </Navbar>
        </>
    )
}

export default NavBar;