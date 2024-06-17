import { Typography } from "@mui/material";
import { useEffect, useState } from "react";
import instance from "../api/api";

export default function Dashboard() {
  const [users, setUsers] = useState([]);
  useEffect(() => {
    const getUsers = async () => {
      const response = await instance.get("");
      const data = await response.json();
      setUsers(data);
    };
    getUsers();
  });
  console.log(users);
  return (
    <>
      <Typography variant="h4" align="center">
        Dashboard
      </Typography>
    </>
  );
}
