import {Link,useNavigate} from "react-router-dom";
import {request} from "../api";

export default function Header({auth,onAuthChange}){
  const nav=useNavigate();

  function goHome(e){
    e.preventDefault();
    nav("/");
    window.scrollTo({top:0,behavior:"smooth"});
  }

  async function logout(){
    await request("/api/auth/logout",{method:"POST"});
    await onAuthChange();
    nav("/");
  }

  return <>
    <header className="header">
      <a className="brand" href="/" onClick={goHome}>
        StyleShop <small>Wear Your Story</small>
      </a>
      <nav>
        <Link to="/cart">🛒 購物車</Link>
        <Link to="/orders">▣ 我的訂單</Link>
        {auth.loggedIn
          ? <button className="linkbtn" onClick={logout}>↪ 登出 ({auth.username})</button>
          : <Link to="/login">♙ 登入</Link>}
      </nav>
    </header>

    {auth.role==="ADMIN"&&<div className="adminbar">
      <a href="/" onClick={goHome}>首頁</a>
      <Link to="/admin/products">商品管理</Link>
      <Link to="/admin/categories">種類管理</Link>
      <a href="http://localhost:8080/api/reports/test.pdf" target="_blank" rel="noreferrer">PDF 測試報表</a>
      <a href="http://localhost:8080/api/reports/login-history.pdf" target="_blank" rel="noreferrer">登入紀錄 PDF</a>
      <a href="http://localhost:8080/api/reports/products.pdf" target="_blank" rel="noreferrer">商品清單 PDF</a>
    </div>}
  </>;
}
