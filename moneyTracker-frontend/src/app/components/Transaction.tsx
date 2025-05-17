import { useState, useEffect } from "react";
import axios from "axios";
import { FaRegMoneyBillAlt } from "react-icons/fa";
import { MdArrowForwardIos } from "react-icons/md";
import { MdOutlineArrowBackIosNew } from "react-icons/md";
import { FaSearch } from "react-icons/fa";

import TransactionModal from "./TransactionModal";

import "../../styles/transaction.css";

interface Transaction {
    name: string;
    amount: number;
    transactionType: string;
    transactionCategory: string;
    date: string;
}

interface PageResponse<T> {
    content: T[];
    totalPages: number;
    totalElements: number;
    number: number;
    size: number;
}

export default function Transaction() {
    const [transactionData, setTransactionData] = useState<Transaction[]>([]);
    const [modalOpen, setModalOpen] = useState<boolean>(false);
    const [page, setPage] = useState<number>(0);
    const [totalPages, setTotalPages] = useState<number>(0);
    const [searchText, setSearchText] = useState<string>("");

    const loadTransactions = async (pageNumber: number = 0) => {
        try {
            const token = localStorage.getItem('jwtToken');

            const response = await axios.get<PageResponse<Transaction>>(`http://localhost:8080/api/transactions/pagination`, {
                headers: {
                    'Authorization': token
                },
                params: {
                    page: pageNumber,
                    size: 10,
                    sort: 'date,desc'
                }
            });

            setTransactionData(response.data.content);
            setPage(response.data.number);
            setTotalPages(response.data.totalPages);
        } catch (error) {
            console.log(error);
        }
    };

    const searchTransactionsByName = async (name: string) => {
        try {
            const token = localStorage.getItem('jwtToken');
            const response = await axios.get<Transaction[]>(`http://localhost:8080/api/transactions/${name}`, {
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
        if (searchText.trim() === '') {
            loadTransactions(0);
        } else {
            searchTransactionsByName(searchText);
        }
    }, [searchText]);

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
                <div className="search-container">
                    <div className="search-input-wrapper">
                        <FaSearch className="search-icon" />
                        <input 
                            type="text" 
                            placeholder="Buscar por nome da transação..." 
                            value={searchText}
                            onChange={(e) => setSearchText(e.target.value)}
                            className="search-input"
                        />
                    </div>
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
                                        <div style={{display: 'flex', alignItems: 'center', gap: '0.75rem'}}>
                                            <FaRegMoneyBillAlt style={{color: '#666'}}/>
                                            {item.name}
                                        </div>
                                    </td>
                                    <td>
                                            <span
                                                className={`transaction-type ${item.transactionType === "DESPESA" ? "expense" : "deposit"}`}>
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
                <div className="pagination-controls">
                    <button onClick={() => loadTransactions(page - 1)} disabled={page === 0}>
                        <MdOutlineArrowBackIosNew />
                    </button>
                    <span>Página {page + 1} de {totalPages}</span>
                    <button onClick={() => loadTransactions(page + 1)} disabled={page + 1 === totalPages}>
                        <MdArrowForwardIos />
                    </button>
                </div>
            </div>

            {modalOpen && <TransactionModal onClose={handleCloseModal}/>}
        </div>
    );

}