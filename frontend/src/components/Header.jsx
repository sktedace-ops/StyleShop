import {Link,useNavigate} from "react-router-dom";
import {request,setAccessToken,downloadBase64Pdf} from "../api";

export default function Header({auth,onAuthChange}){
  const nav=useNavigate();

  function goHome(e){
    e.preventDefault();
    nav("/");
    window.scrollTo({top:0,behavior:"smooth"});
  }

  async function logout(){
    try{await request("/api/auth/logout",{method:"POST"})}catch{}
    setAccessToken("");
    await onAuthChange();
    nav("/login",{replace:true});
  }

  async function exportPdf(path){
    try{
      await downloadBase64Pdf(path);
    }catch(error){
      alert(error.message || "PDF 匯出失敗");
    }
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
      <button className="linkbtn" onClick={()=>exportPdf("/api/reports/login-history/base64?lang=zh-TW")}>登入紀錄 中文 PDF</button>
      <button className="linkbtn" onClick={()=>exportPdf("/api/reports/login-history/base64?lang=en")}>Login History EN</button>
      <button className="linkbtn" onClick={()=>exportPdf("/api/reports/products/base64?lang=zh-TW")}>商品清單 中文 PDF</button>
      <button className="linkbtn" onClick={()=>exportPdf("/api/reports/products/base64?lang=en")}>Products EN</button>
    </div>}
  </>;
}
