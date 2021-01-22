import React, { useState } from "react";
import axios from "axios";

export default function App() {

    const [users, setUsers] = useState([])
    const url = "http://localhost:3000/interestedUsers"
    let getUsers = () => {
        axios
            .post(postpone_url, data)
            .then((res) => {return res})
            .catch((err) => console.log(err.data));
    };

    useEffect(() => {
        this.setUsers = getUsers
      });

    return (
        <div>
            {users.map((item, index) => {
                <label>{item}</label>
            })}
        </div>

    );
}