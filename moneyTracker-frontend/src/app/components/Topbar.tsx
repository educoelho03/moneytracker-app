import { useState } from "react";
import { useNavigate } from "react-router-dom"; 
import LogoutModal from "./LogoutModal";
import { IoHomeOutline } from "react-icons/io5";
import { FaRegMoneyBillAlt } from "react-icons/fa";
import { HiUser } from "react-icons/hi";

import "../../styles/topbar.css";

export default function Topbar() {
    const [modalOpen, setModalOpen] = useState<boolean>(false);
    const userName = localStorage.getItem("name");
    const navigate = useNavigate();

    const closeModal = () => {
        setModalOpen(false);
    }

    const openModal = () => {
        setModalOpen(true);
    }

    return (
        <div className="topbar-container">
            <div className="topbar-content">
                <img src="src/assets/svg/money-tracker-logo.png" className="topbar-logo-image" alt="Logo" onClick={() => navigate("/dashboard")}/>
                <div className="topbar-links">
                    <button onClick={() => navigate("/dashboard")}>
                        <IoHomeOutline className="nav-icon" />
                        Dashboard
                    </button> 
                    <button onClick={() => navigate("/transacoes")}>
                        <FaRegMoneyBillAlt className="nav-icon" />
                        Transações
                    </button>
                </div>
                <div className="topbar-user">
                    <button onClick={openModal}>
                        <HiUser className="nav-icon" />
                        Olá, {userName}
                    </button>
                </div>
            </div>

            {modalOpen && <LogoutModal onClose={closeModal} />}
        </div>
    );
}