import { useState } from "react";
import { AppUserCreateRequest } from "../../types/AppUser/AppUserCreateRequest";
import { register } from "../../api/AppUserAPI";
import { RegisterRequest } from "../../types/AppUser/RegisterRequest";
import { useNavigate } from "react-router-dom";

const Register: React.FC = () => {


    const [credentials, setCredentials] = useState<RegisterRequest>({
        username: "",
        email: "",
        password: "",
        roles: []
    })

    const [errors, setErrors] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState<string | null>(null);
    const navigate = useNavigate();

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        setLoading(true);
        setErrors(null);

        try {
            const data = await register(credentials);
            console.log(data);
            setMessage("Registration Complete");
            navigate("/verify")
            localStorage.setItem("email", credentials.email);
        } catch (e) {
            setErrors((e as Error).message)
            setMessage("Registration Failed")
        } finally {
            setLoading(false);
        }
    }



    return (
        <>
            <div>
                <h2 className="text-xl font-bold mb-2">Register</h2>

                <div className="p-4">
                    <form onSubmit={handleSubmit}>
                        <h2 className="text-xl font-bold mb-2">Enter Username</h2>
                        <input
                            type="text"
                            value={credentials.username}
                            onChange={(e) => setCredentials((prev) => ({ ...prev, username: e.target.value }))}
                            className="border px-2 py-1 mr-2"
                            placeholder="Enter Username"
                            required
                        />
                        <h2 className="text-xl font-bold mb-2">Enter Email</h2>
                        <input
                            type="email"
                            value={credentials.email}
                            onChange={(e) => setCredentials((prev) => ({ ...prev, email: e.target.value }))}
                            className="border px-2 py-1 mr-2"
                            placeholder="Enter Email"
                            required
                        />
                        <h2 className="text-xl font-bold mb-2">Enter Password</h2>
                        <input
                            type="password"
                            value={credentials.password}
                            onChange={(e) => setCredentials((prev) => ({ ...prev, password: e.target.value }))}
                            className="border px-2 py-1 mr-2"
                            placeholder="Enter Password"
                            required
                        />
                        <button onClick={() => setCredentials((prev) => ({...prev, roles: ["USER"]}))} type="submit" disabled={loading} className="bg-blue-600 text-white px-3 py-1 rounded">
                            {loading ? "Registering..." : "Register"}
                        </button>
                        <button onClick={() => setCredentials((prev) => ({...prev, roles: ["USER", "SELLER"]}))} type="submit" disabled={loading} className="bg-blue-600 text-white px-3 py-1 rounded">
                            {loading ? "Registering..." : "Register as Seller"}
                        </button>

                    </form>


                    {message && <p className="text-red-600 mt-2">{message}</p>}
                    {errors && <p className="text-red-600 mt-2">{errors}</p>}
                </div>
            </div>

        </>
    )
}

export default Register;