import { useState, useEffect } from "react";
import axios from "axios";
import { FaRegMoneyBillAlt } from "react-icons/fa";
import TransactionModal from "./TransactionModal";

import "../../styles/transaction.css";

interface Transaction {
    name: string;
    amount: number;
    transactionType: string;
    transactionCategory: string;
    date: string;
}

export default function Transaction() {
    const [transactionData, setTransactionData] = useState<Transaction[]>([]);
    const [modalOpen, setModalOpen] = useState<boolean>(false);

    const loadTransactions = async () => {
        try {
            const token = localStorage.getItem('jwtToken');

            const response = await axios.get(`${import.meta.env.VITE_API_BASE_URL}/api/transactions`, {
                headers: {
                    'Authorization': token
                }
            });

            setTransactionData(response.data);
        } catch (error) {
            console.log(error);
        }
    };

    useEffect(() => {
        loadTransactions();
    }, []);

    const handleAddTransaction = () => {
        setModalOpen(true);
    };

    const handleCloseModal = () => {
        setModalOpen(false);
        loadTransactions(); // Reload transactions after adding a new one
    };

    const formatDate = (dateString: string): string => {
        const date = new Date(dateString);
        return date.toLocaleDateString('pt-BR', {
            day: '2-digit',
            month: 'short',
            year: 'numeric'
        }).replace('.', '');
    };

    const formatAmount = (amount: number, type: string): string => {
        const formattedAmount = amount.toFixed(2);
        return type === "DESPESA" ? `-R$ ${formattedAmount}` : `R$ ${formattedAmount}`;
    };

    return (
        <div className="transaction-container">
            <div className="transaction-content">
                <div className="transaction-header">
                    <div className="title">Transações</div>
                    <button className="transaction-button" onClick={handleAddTransaction}>
                        Adicionar transação
                    </button>
                </div>

                <div className="transaction-table">
                    <table className="transaction">
                        <thead>
                            <tr>
                                <th>Nome</th>
                                <th>Tipo</th>
                                <th>Categoria</th>
                                <th>Data</th>
                                <th>Valor</th>
                            </tr>
                        </thead>
                        <tbody>
                            {transactionData.length === 0 ? (
                                <tr>
                                    <td colSpan={5} className="no-records-message">
                                        Nenhum registro encontrado.
                                    </td>
                                </tr>
                            ) : (
                                transactionData.map((item, index) => (
                                    <tr key={index}>
                                        <td>
                                            <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                                                <FaRegMoneyBillAlt style={{ color: '#666' }} />
                                                {item.name}
                                            </div>
                                        </td>
                                        <td>
                                            <span className={`transaction-type ${item.transactionType === "DESPESA" ? "expense" : "deposit"}`}>
                                                {item.transactionType === "DESPESA" ? "Despesa" : "Depósito"}
                                            </span>
                                        </td>
                                        <td>
                                            <span className="transaction-category">
                                                {item.transactionCategory}
                                            </span>
                                        </td>
                                        <td>{formatDate(item.date)}</td>
                                        <td style={{ 
                                            color: item.transactionType === "DESPESA" ? "#e93030" : "#55b02e",
                                            fontWeight: 600
                                        }}>
                                            {formatAmount(item.amount, item.transactionType)}
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
            </div>

            {modalOpen && <TransactionModal onClose={handleCloseModal} />}
        </div>
    );
}