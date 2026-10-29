import {useNavigate} from "react-router-dom";

export default function PageActions(){
  const navigate = useNavigate();

  function goBack(){
    if(window.history.length > 1){
      navigate(-1);
    }else{
      navigate("/");
    }
  }

  function refreshPage(){
    window.location.reload();
  }

  function goHome(){
    navigate("/");
    window.scrollTo({top:0,behavior:"smooth"});
  }

  return (
    <div className="page-actions">
      <button type="button" onClick={goBack}>← 上一頁</button>
      <button type="button" onClick={refreshPage}>↻ 重新整理</button>
      <button type="button" className="primary" onClick={goHome}>⌂ 回到首頁</button>
    </div>
  );
}
